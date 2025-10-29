package com.cesarmarcos.petshop.controller;

import com.cesarmarcos.petshop.utils.ErrorCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@Log4j2

@RestControllerAdvice
public class ExceptionRestController {
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<?> handleEntityNotFoundException() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCode.ENTITY_NOT_FOUND);
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex) {
    log.error ("Error inesperado: {}", ex.getMessage(), ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
  }
}
