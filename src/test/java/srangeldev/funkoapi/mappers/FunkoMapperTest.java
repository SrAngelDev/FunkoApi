package srangeldev.funkoapi.mappers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import srangeldev.funkoapi.dto.FunkoResponseDto;
import srangeldev.funkoapi.models.Funko;
import srangeldev.funkoapi.models.enums.Categoria;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FunkoMapperTest {

    // Instanciamos el mapper directamente, ya que no tiene dependencias
    private final FunkoMapper funkoMapper = new FunkoMapper();

    @Test
    @DisplayName("Debe mapear un Funko a FunkoResponseDto correctamente")
    void toResponse_withValidFunko_mapsCorrectly() {
        // Arrange: creamos un objeto Funko de origen con datos de prueba
        Funko funko = new Funko(
                1L,
                "Batman",
                99.99,
                Categoria.COMICS,
                LocalDate.of(2023, 10, 1),
                LocalDateTime.of(2023, 10, 1, 10, 0, 0),
                LocalDateTime.of(2023, 10, 5, 12, 30, 0)
        );

        // Act: llamamos al método que queremos probar
        FunkoResponseDto dto = funkoMapper.toResponse(funko);

        // Assert: verificamos que el resultado no es nulo y que cada campo coincide
        assertNotNull(dto);
        assertAll(
                () -> assertEquals(funko.getId(), dto.getId()),
                () -> assertEquals(funko.getNombre(), dto.getNombre()),
                () -> assertEquals(funko.getPrecio(), dto.getPrecio()),
                () -> assertEquals(funko.getCategoria(), dto.getCategoria()),
                () -> assertEquals(funko.getFechaLanzamiento(), dto.getFechaLanzamiento()),
                () -> assertEquals(funko.getCreatedAt(), dto.getCreatedAt()),
                () -> assertEquals(funko.getUpdatedAt(), dto.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Debe devolver null si el Funko de entrada es null")
    void toResponse_withNullFunko_returnsNull() {
        // Arrange: el objeto Funko es nulo
        Funko funko = null;

        // Act: llamamos al método
        FunkoResponseDto dto = funkoMapper.toResponse(funko);

        // Assert: verificamos que el resultado es nulo
        assertNull(dto);
    }
}