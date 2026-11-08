package com.kevin.monitorprecios.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("usuarios")
public class Usuario {

    @Id
    private String id;

    private String nombre;

    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private Rol rol = Rol.CLIENTE;

    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
