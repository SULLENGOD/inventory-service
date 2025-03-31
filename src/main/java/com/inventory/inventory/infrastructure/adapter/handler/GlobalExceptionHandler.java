package com.inventory.inventory.infrastructure.adapter.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.inventory.inventory.domain.exception.FileNotCreated;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(FileNotCreated.class)
    public ResponseEntity<String> handleFileNotCreated(FileNotCreated ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
