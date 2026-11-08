package com.kevin.monitorprecios.controller;

import com.kevin.monitorprecios.dto.ProductoRequest;
import com.kevin.monitorprecios.model.Producto;
import com.kevin.monitorprecios.service.ProductoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<Producto> listar(Authentication sesion) {
        return productoService.listar(sesion.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@RequestBody ProductoRequest request, Authentication sesion) {
        return productoService.crear(request, sesion.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id, Authentication sesion) {
        productoService.eliminar(id, sesion.getName());
    }
}
