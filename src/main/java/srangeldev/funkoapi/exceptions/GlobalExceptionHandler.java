package srangeldev.funkoapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manejador global de excepciones adaptado al formato solicitado.
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FunkoNotFoundException.class)
    public Map<String, String> handleNotFoundExceptions(FunkoNotFoundException ex) {
        log.info("MANEJADOR DE EXCEPCIONES: Convirtiendo FunkoNotFoundException en 404 Not Found");
        return Map.of("error", ex.getMessage());
    }
}
