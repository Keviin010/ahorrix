package com.kevin.monitorprecios.scraper;

import java.math.BigDecimal;

public record Lectura(Estado estado, BigDecimal precio, String nombre) {

    public enum Estado {
        DISPONIBLE, AGOTADO, RETIRADO, SIN_DATOS
    }

    public Lectura {
        nombre = nombre == null ? "" : nombre;
    }

    public Lectura(Estado estado, BigDecimal precio) {
        this(estado, precio, "");
    }

    public static Lectura sinPrecio(Estado estado) {
        return new Lectura(estado, null, "");
    }
}
