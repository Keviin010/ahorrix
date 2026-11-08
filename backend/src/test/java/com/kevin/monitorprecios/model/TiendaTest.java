package com.kevin.monitorprecios.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class TiendaTest {

    @Test
    void reconoceExito() {
        assertEquals(Tienda.EXITO, Tienda.de("https://www.exito.com/televisor-123/p"));
    }

    @Test
    void reconoceAlkostoSinWww() {
        assertEquals(Tienda.ALKOSTO, Tienda.de("https://alkosto.com/celular-xyz/p"));
    }

    @Test
    void reconoceMattelsaEnMayusculas() {
        assertEquals(Tienda.MATTELSA, Tienda.de("HTTPS://WWW.MATTELSA.NET/camiseta"));
    }

    @Test
    void unaTiendaNoSoportadaDaError400() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> Tienda.de("https://www.mercadolibre.com.co/producto"));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void unEnlaceInvalidoDaError400() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> Tienda.de("esto no es un enlace"));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }
}
