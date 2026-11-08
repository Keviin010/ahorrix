package com.kevin.monitorprecios.exception;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> manejar(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("mensaje", e.getReason()));
    }
}
