package com.kevin.monitorprecios.service;

import static org.springframework.http.HttpStatus.*;

import com.kevin.monitorprecios.dto.AuthRequest;
import com.kevin.monitorprecios.model.Usuario;
import com.kevin.monitorprecios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email()).orElse(null);

        if (usuario == null || !passwordCorrecta(usuario, request.password())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Correo o contraseña incorrectos");
        }

        return usuario;
    }

    public Usuario registrar(AuthRequest request) {
        if (request.nombre().isBlank() || request.email().isBlank() || request.password().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Todos los campos son obligatorios");
        }

        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(BAD_REQUEST, "El correo ya está registrado");
        }

        validarPassword(request.password());

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        return usuarioRepository.save(usuario);
    }

    private void validarPassword(String password) {
        if (password.length() < 6 || password.length() > 72) {
            throw new ResponseStatusException(BAD_REQUEST, "La contraseña debe tener entre 6 y 72 caracteres");
        }
    }

    private boolean passwordCorrecta(Usuario usuario, String password) {
        String guardada = usuario.getPassword();
        return guardada != null && passwordEncoder.matches(password, guardada);
    }
}
