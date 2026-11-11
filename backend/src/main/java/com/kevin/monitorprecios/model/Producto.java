package com.kevin.monitorprecios.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("productos")
@CompoundIndex(name = "usuario_url", def = "{'emailUsuario': 1, 'url': 1}", unique = true)
public class Producto {

    @Id
    private String id;

    private String emailUsuario;

    private String nombre;

    private String url;

    private String tienda;

    private BigDecimal precioInicial;
    private BigDecimal precioActual;
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
