package srangeldev.funkoapi.Categoria.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import srangeldev.funkoapi.Categoria.dto.CategoriaRequestDto;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;
import srangeldev.funkoapi.Categoria.models.Categoria;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaMapperTest {

    // Instanciamos el mapper directamente, ya que no tiene dependencias
    private CategoriaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoriaMapper();
    }

    @Test
    @DisplayName("Debe mapear CategoriaRequestDto a Categoria")
    void toCategoria() {
        // Arrange: Creamos el DTO de entrada
        CategoriaRequestDto dto = new CategoriaRequestDto("SERIES");

        // Act: Llamamos al método
        Categoria categoria = mapper.toCategoria(dto);

        // Assert: Verificamos que los campos se mapearon correctamente
        assertNotNull(categoria);
        assertNull(categoria.getId()); // El ID debe ser nulo en la creación
        assertEquals("SERIES", categoria.getNombre());
        assertNull(categoria.getCreatedAt()); // Las fechas las gestiona JPA
        assertNull(categoria.getUpdatedAt()); // Las fechas las gestiona JPA
    }

    @Test
    @DisplayName("Debe mapear Categoria a CategoriaResponseDto")
    void toResponseDto() {
        // Arrange: Creamos la entidad Categoria (como vendría de la BBDD)
        LocalDateTime now = LocalDateTime.now();
        Categoria categoria = new Categoria(
                1L,
                "PELICULAS",
                now.minusDays(1),
                now
        );

        // Act: Llamamos al método
        CategoriaResponseDto dto = mapper.toResponseDto(categoria);

        // Assert: Verificamos que todos los campos se mapearon
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("PELICULAS", dto.getNombre());
        assertEquals(categoria.getCreatedAt(), dto.getCreatedAt());
        assertEquals(categoria.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    @DisplayName("toResponseDto debe manejar null")
    void toResponseDtoNull() {
        // Arrange
        Categoria categoria = null;

        // Act & Assert
        // Aunque tu implementación actual lanzaría un NullPointerException
        // (lo cual es aceptable), si quisieras manejarlo, este test fallaría.
        // Para esta implementación, lo correcto es esperar la excepción.
        assertThrows(NullPointerException.class, () -> {
            mapper.toResponseDto(categoria);
        });

        // Si el mapper se modificara para ser defensivo:
        // if (categoria == null) return null;
        // CategoriaResponseDto dto = mapper.toResponseDto(categoria);
        // assertNull(dto);
    }

    @Test
    @DisplayName("toCategoria debe manejar null")
    void toCategoriaNull() {
        // Arrange
        CategoriaRequestDto dto = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            mapper.toCategoria(dto);
        });
    }
}
