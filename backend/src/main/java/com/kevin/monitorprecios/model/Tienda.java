package com.kevin.monitorprecios.model;

import java.net.URI;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public enum Tienda {

    EXITO("Éxito", "exito.com"),
    ALKOSTO("Alkosto", "alkosto.com"),
    MATTELSA("Mattelsa", "mattelsa.net");

    private final String nombre;
    private final String dominio;

    Tienda(String nombre, String dominio) {
        this.nombre = nombre;
        this.dominio = dominio;
    }

    public static Tienda de(String url) {
        String host;
        try {
            host = URI.create(url).getHost().replaceFirst("^www\\.", "").toLowerCase();
        } catch (Exception e) {
            host = "";
        }

        for (Tienda tienda : values()) {
            if (host.endsWith(tienda.dominio)) {
                return tienda;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Tienda no soportada. Usa Éxito, Alkosto o Mattelsa.");
    }
}
