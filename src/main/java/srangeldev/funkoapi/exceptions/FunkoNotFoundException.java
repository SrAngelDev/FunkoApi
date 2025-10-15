package srangeldev.funkoapi.exceptions;

/**
 * Excepción específica para indicar que un Funko no existe.
 */
public class FunkoNotFoundException extends RuntimeException {
    public FunkoNotFoundException(Long id) {
        super("Funko con id " + id + " no encontrado");
    }
}
