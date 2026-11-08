package com.kevin.monitorprecios.dto;

public record ProductoRequest(String url, String nombre) {

    public ProductoRequest {
        url = url == null ? "" : url.trim();
        nombre = nombre == null ? "" : nombre.trim();
    }
}
