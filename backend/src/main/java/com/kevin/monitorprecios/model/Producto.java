package com.kevin.monitorprecios.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("productos")
public class Producto {

    @Id
    private String id;

    private String emailUsuario;

    private String nombre;

    @Indexed(unique = true)
    private String url;

    private String tienda;

    private BigDecimal precioInicial;
    private BigDecimal precioActual;
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
