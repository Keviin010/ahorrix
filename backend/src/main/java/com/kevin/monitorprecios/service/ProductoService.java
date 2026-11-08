package com.kevin.monitorprecios.service;

import static org.springframework.http.HttpStatus.*;

import com.kevin.monitorprecios.dto.ProductoRequest;
import com.kevin.monitorprecios.model.Producto;
import com.kevin.monitorprecios.model.Tienda;
import com.kevin.monitorprecios.repository.ProductoRepository;
import com.kevin.monitorprecios.scraper.Lectura;
import com.kevin.monitorprecios.scraper.PrecioScraper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final PrecioScraper scraper;

    public List<Producto> listar(String email) {
        return productoRepository.findByEmailUsuarioIgnoreCaseOrderByFechaRegistroDesc(email);
    }

    public Producto crear(ProductoRequest request, String email) {
        if (request.url().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "El enlace del producto es obligatorio");
        }

        if (productoRepository.existsByUrl(request.url())) {
            throw new ResponseStatusException(BAD_REQUEST, "Ese producto ya está siendo vigilado");
        }

        Tienda tienda = Tienda.de(request.url());

        Lectura lectura = scraper.leer(request.url());

        if (lectura.estado() == Lectura.Estado.RETIRADO) {
            throw new ResponseStatusException(BAD_REQUEST, "Ese enlace ya no existe en la tienda. Revisa que esté bien copiado.");
        }

        if (lectura.estado() == Lectura.Estado.AGOTADO) {
            throw new ResponseStatusException(BAD_REQUEST, "Ese producto está agotado en la tienda.");
        }

        if (lectura.estado() == Lectura.Estado.SIN_DATOS) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No pudimos leer el precio. Usa un enlace de Éxito, Alkosto o Mattelsa con precio visible.");
        }

        String nombre = request.nombre().isBlank() ? lectura.nombre() : request.nombre();
        if (nombre.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No pudimos leer el nombre del producto. Escríbelo tú en el formulario.");
        }

        Producto producto = new Producto();
        producto.setEmailUsuario(email);
        producto.setNombre(nombre);
        producto.setUrl(request.url());
        producto.setTienda(tienda.getNombre());
        producto.setPrecioInicial(lectura.precio());
        producto.setPrecioActual(lectura.precio());
        return productoRepository.save(producto);
    }

    public void eliminar(String id, String email) {
        Producto producto = productoRepository.findById(id)
                .filter(p -> p.getEmailUsuario().equalsIgnoreCase(email.trim()))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado"));

        productoRepository.delete(producto);
    }
}
