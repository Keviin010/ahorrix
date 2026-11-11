package com.kevin.monitorprecios.repository;

import com.kevin.monitorprecios.model.Producto;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    List<Producto> findByEmailUsuarioIgnoreCaseOrderByFechaRegistroDesc(String email);

    boolean existsByEmailUsuarioIgnoreCaseAndUrl(String email, String url);
}
