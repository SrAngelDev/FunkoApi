package srangeldev.funkoapi.expections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaException;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaNotFoundException;
import srangeldev.funkoapi.Funko.exceptions.FunkoException;
import srangeldev.funkoapi.Funko.exceptions.FunkoNotFoundException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        // Instanciamos la clase directamente
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleValidationExceptions: Devuelve mapa de errores de campo")
    void handleValidationExceptions() {
        // Arrange
        // 1. Crear los errores de campo
        FieldError fieldError1 = new FieldError("dto", "nombre", "El nombre no puede estar vacío");
        FieldError fieldError2 = new FieldError("dto", "precio", "El precio debe ser positivo");

        // 2. Simular el BindingResult
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // 3. Simular la excepción principal
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // Act
        Map<String, String> errors = exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("El nombre no puede estar vacío", errors.get("nombre"));
        assertEquals("El precio debe ser positivo", errors.get("precio"));
    }

    @Test
    @DisplayName("handleNotFoundExceptions: Maneja FunkoNotFound")
    void handleNotFoundExceptions_Funko() {
        // Arrange
        FunkoNotFoundException exception = new FunkoNotFoundException(99L);
        String expectedMessage = "Funko con id 99 no encontrado";

        // Act
        Map<String, String> error = exceptionHandler.handleNotFoundExceptions(exception);

        // Assert
        assertNotNull(error);
        assertEquals(1, error.size());
        assertEquals(expectedMessage, error.get("error"));
    }

    @Test
    @DisplayName("handleNotFoundExceptions: Maneja CategoriaNotFound")
    void handleNotFoundExceptions_Categoria() {
        // Arrange
        CategoriaNotFoundException exception = new CategoriaNotFoundException(88L);
        String expectedMessage = "No se pudo encontrar la categoría con el ID: 88";

        // Act
        Map<String, String> error = exceptionHandler.handleNotFoundExceptions(exception);

        // Assert
        assertNotNull(error);
        assertEquals(1, error.size());
        assertEquals(expectedMessage, error.get("error"));
    }

    @Test
    @DisplayName("handleConflictExceptions: Maneja CategoriaException (409)")
    void handleConflictExceptions() {
        // Arrange
        CategoriaException exception = new CategoriaException("El nombre ya existe");
        String expectedMessage = "El nombre ya existe";

        // Act
        Map<String, String> error = exceptionHandler.handleConflictExceptions(exception);

        // Assert
        assertNotNull(error);
        assertEquals(1, error.size());
        assertEquals(expectedMessage, error.get("error"));
    }

    @Test
    @DisplayName("handleBusinessExceptions: Maneja FunkoException (400)")
    void handleBusinessExceptions() {
        // Arrange
        FunkoException exception = new FunkoException("El precio debe ser positivo");
        String expectedMessage = "El precio debe ser positivo";

        // Act
        Map<String, String> error = exceptionHandler.handleBusinessExceptions(exception);

        // Assert
        assertNotNull(error);
        assertEquals(1, error.size());
        assertEquals(expectedMessage, error.get("error"));
    }
}
