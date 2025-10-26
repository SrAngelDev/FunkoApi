package srangeldev.funkoapi.Funko.mappers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;
import srangeldev.funkoapi.Categoria.mappers.CategoriaMapper;
import srangeldev.funkoapi.Categoria.models.Categoria;
import srangeldev.funkoapi.Funko.dto.FunkoResponseDto;
import srangeldev.funkoapi.Funko.models.Funko;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unitario para FunkoMapper.
 * Usamos Mockito para simular la dependencia CategoriaMapper.
 */
@ExtendWith(MockitoExtension.class) // Habilita Mockito
class FunkoMapperTest {

    @Mock
    private CategoriaMapper categoriaMapper; // La dependencia que simulamos

    @InjectMocks
    private FunkoMapper funkoMapper; // La clase que estamos probando

    // Datos de prueba que coinciden con los modelos reales
    private final Categoria categoriaEntidad = new Categoria(
            1L,
            "SERIES",
            LocalDateTime.of(2023, 1, 1, 10, 0),
            LocalDateTime.of(2023, 1, 1, 11, 0)
    );

    private final CategoriaResponseDto categoriaDto = new CategoriaResponseDto(
            1L,
            "SERIES",
            categoriaEntidad.getCreatedAt(),
            categoriaEntidad.getUpdatedAt()
    );

    private final Funko funkoEntidad = new Funko(
            1L,
            UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"),
            "Batman",
            99.99,
            categoriaEntidad, // Usamos la entidad Categoria
            LocalDate.of(2023, 10, 1),
            LocalDateTime.of(2023, 10, 1, 10, 0, 0),
            LocalDateTime.of(2023, 10, 5, 12, 30, 0)
    );

    @Test
    @DisplayName("Debe mapear un Funko a FunkoResponseDto correctamente")
    void toResponse_withValidFunko_mapsCorrectly() {
        // Arrange:
        // Configuramos el mock: cuando se llame a toResponseDto con la entidad Categoria,
        // debe devolver el DTO de Categoria.
        when(categoriaMapper.toResponseDto(categoriaEntidad)).thenReturn(categoriaDto);

        // Act: llamamos al método que queremos probar
        FunkoResponseDto dto = funkoMapper.toResponse(funkoEntidad);

        // Assert: verificamos que el resultado no es nulo y que cada campo coincide
        assertNotNull(dto);
        assertAll(
                () -> assertEquals(funkoEntidad.getId(), dto.getId()),
                () -> assertEquals(funkoEntidad.getUuid(), dto.getUuid()),
                () -> assertEquals(funkoEntidad.getNombre(), dto.getNombre()),
                () -> assertEquals(funkoEntidad.getPrecio(), dto.getPrecio()),
                () -> assertEquals(funkoEntidad.getFechaLanzamiento(), dto.getFechaLanzamiento()),
                () -> assertEquals(funkoEntidad.getCreatedAt(), dto.getCreatedAt()),
                () -> assertEquals(funkoEntidad.getUpdatedAt(), dto.getUpdatedAt()),

                // Verificamos que el DTO de categoría anidado es correcto
                () -> assertNotNull(dto.getCategoria()),
                () -> assertEquals(categoriaDto.getId(), dto.getCategoria().getId()),
                () -> assertEquals(categoriaDto.getNombre(), dto.getCategoria().getNombre())
        );

        // Verificamos que el método del mapper simulado fue llamado
        verify(categoriaMapper).toResponseDto(categoriaEntidad);
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

    @Test
    @DisplayName("Debe mapear Funko con Categoria nula")
    void toResponse_withNullCategoria_mapsCorrectly() {
        // Arrange:
        // Creamos un Funko cuya categoría es nula
        Funko funkoSinCategoria = new Funko(
                2L,
                UUID.fromString("a47ac10b-58cc-4372-a567-0e02b2c3d470"),
                "Joker",
                89.99,
                null, // Categoria nula
                LocalDate.of(2023, 11, 1),
                LocalDateTime.of(2023, 11, 1, 10, 0, 0),
                LocalDateTime.of(2023, 11, 5, 12, 30, 0)
        );

        // Act:
        FunkoResponseDto dto = funkoMapper.toResponse(funkoSinCategoria);

        // Assert:
        assertNotNull(dto);
        assertEquals(funkoSinCategoria.getNombre(), dto.getNombre());
        // Verificamos que el campo Categoria en el DTO es nulo,
        // como se espera de la lógica del mapper.
        assertNull(dto.getCategoria());
    }
}
