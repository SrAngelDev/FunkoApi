package srangeldev.funkoapi.Funko.exceptions;

/**
 * Excepción base para errores relacionados con Funkos.
 */
public class FunkoException extends RuntimeException {
    public FunkoException(String message) {
        super(message);
    }
}
