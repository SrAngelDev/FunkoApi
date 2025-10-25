package srangeldev.funkoapi.expections;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaException;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaNotFoundException;
import srangeldev.funkoapi.Funko.exceptions.FunkoException;
import srangeldev.funkoapi.Funko.exceptions.FunkoNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manejador global de excepciones para toda la API.
 * Captura excepciones de Funko, Categoria y Validación.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.info("MANEJADOR DE EXCEPCIONES: Convirtiendo MethodArgumentNotValidException en 400 Bad Request");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Handler para todas las excepciones "No Encontrado" (404).
     * Combina FunkoNotFound y CategoriaNotFound.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({FunkoNotFoundException.class, CategoriaNotFoundException.class})
    public Map<String, String> handleNotFoundExceptions(RuntimeException ex) {
        log.info("MANEJADOR DE EXCEPCIONES: Convirtiendo " + ex.getClass().getSimpleName() + " en 404 Not Found");
        return Map.of("error", ex.getMessage());
    }

    /**
     * Handler para excepciones de conflicto, el 409
     * Usado cuando se intenta crear un recurso que ya existe (ej. Categoria con nombre duplicado)
     * o borrar un recurso con dependencias (ej. Categoria con Funkos).
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CategoriaException.class)
    public Map<String, String> handleConflictExceptions(CategoriaException ex) {
        log.info("MANEJADOR DE EXCEPCIONES: Convirtiendo CategoriaException en 409 Conflict");
        return Map.of("error", ex.getMessage());
    }

    /**
     * Handler para cosas genericas, el 400 Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FunkoException.class)
    public Map<String, String> handleBusinessExceptions(FunkoException ex) {
        log.info("MANEJADOR DE EXCEPCIONES: Convirtiendo FunkoException en 400 Bad Request");
        return Map.of("error", ex.getMessage());
    }
}