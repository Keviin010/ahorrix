package com.kevin.monitorprecios.dto;

public record AuthRequest(String nombre, String email, String password, String token) {

    public AuthRequest {
        nombre = nombre == null ? "" : nombre.trim();
        email = email == null ? "" : email.trim();
        password = password == null ? "" : password;
        token = token == null ? "" : token.trim();
    }
}
