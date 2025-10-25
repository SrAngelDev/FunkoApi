package srangeldev.funkoapi.Categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoriaException extends RuntimeException {
    public CategoriaException(String message) {
        super(message);
    }
}