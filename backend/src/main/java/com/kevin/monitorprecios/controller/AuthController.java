package com.kevin.monitorprecios.controller;

import com.kevin.monitorprecios.dto.AuthRequest;
import com.kevin.monitorprecios.model.Usuario;
import com.kevin.monitorprecios.security.JwtUtil;
import com.kevin.monitorprecios.service.UsuarioService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest request) {
        Usuario usuario = usuarioService.login(request);
        return Map.of("usuario", usuario, "token", jwtUtil.generar(usuario));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody AuthRequest request) {
        Usuario usuario = usuarioService.registrar(request);
        return Map.of("usuario", usuario, "token", jwtUtil.generar(usuario));
    }
}
