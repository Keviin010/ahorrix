package com.kevin.monitorprecios.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kevin.monitorprecios.model.Rol;
import com.kevin.monitorprecios.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private static final String CLAVE = "una-clave-de-prueba-con-mas-de-32-caracteres";

    private Usuario usuario(Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setEmail("kevin@correo.com");
        usuario.setRol(rol);
        return usuario;
    }

    @Test
    void elTokenGuardaElCorreoYElRol() {
        JwtUtil jwtUtil = new JwtUtil(CLAVE, 8);

        Claims datos = jwtUtil.leer(jwtUtil.generar(usuario(Rol.ADMIN)));

        assertEquals("kevin@correo.com", datos.getSubject());
        assertEquals("ADMIN", datos.get("rol", String.class));
    }

    @Test
    void unTokenAlteradoSeRechaza() {
        JwtUtil jwtUtil = new JwtUtil(CLAVE, 8);
        String token = jwtUtil.generar(usuario(Rol.CLIENTE));

        assertThrows(JwtException.class, () -> jwtUtil.leer(token + "x"));
    }

    @Test
    void unTokenFirmadoConOtraClaveSeRechaza() {
        String token = new JwtUtil(CLAVE, 8).generar(usuario(Rol.CLIENTE));
        JwtUtil otro = new JwtUtil("otra-clave-distinta-con-mas-de-32-caracteres", 8);

        assertThrows(JwtException.class, () -> otro.leer(token));
    }

    @Test
    void unTokenVencidoSeRechaza() {
        JwtUtil jwtUtil = new JwtUtil(CLAVE, -1);

        String token = jwtUtil.generar(usuario(Rol.CLIENTE));

        assertThrows(JwtException.class, () -> jwtUtil.leer(token));
    }

    @Test
    void textoQueNoEsUnTokenSeRechaza() {
        JwtUtil jwtUtil = new JwtUtil(CLAVE, 8);

        assertThrows(JwtException.class, () -> jwtUtil.leer("esto-no-es-un-token"));
    }

    @Test
    void sinClaveConfiguradaUsaUnaTemporalQueFunciona() {
        JwtUtil jwtUtil = new JwtUtil("", 8);

        String token = jwtUtil.generar(usuario(Rol.CLIENTE));

        assertNotNull(token);
        assertEquals("kevin@correo.com", jwtUtil.leer(token).getSubject());
    }
}
