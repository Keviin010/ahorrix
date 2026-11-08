package com.kevin.monitorprecios.security;

import com.kevin.monitorprecios.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey clave;
    private final long duracionMs;

    public JwtUtil(@Value("${app.jwt.secret:}") String secreto, @Value("${app.jwt.horas:8}") long horas) {
        if (secreto.length() < 32) {
            log.warn("No hay app.jwt.secret (minimo 32 caracteres). Se usa una clave temporal y las sesiones se pierden al reiniciar");
            byte[] aleatorio = new byte[32];
            new SecureRandom().nextBytes(aleatorio);
            this.clave = Keys.hmacShaKeyFor(aleatorio);
        } else {
            this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        }
        this.duracionMs = horas * 60 * 60 * 1000;
    }

    public String generar(Usuario usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol().name())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracionMs))
                .signWith(clave)
                .compact();
    }

    public Claims leer(String token) {
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
    }
}
