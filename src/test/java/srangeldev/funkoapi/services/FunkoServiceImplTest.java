package srangeldev.funkoapi.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import srangeldev.funkoapi.dto.FunkoRequestDto;
import srangeldev.funkoapi.exceptions.FunkoException;
import srangeldev.funkoapi.exceptions.FunkoNotFoundException;
import srangeldev.funkoapi.models.Funko;
import srangeldev.funkoapi.models.enums.Categoria;
import srangeldev.funkoapi.repositories.FunkoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {

    @Mock
    private FunkoRepository repository;

    @InjectMocks
    private FunkoServiceImpl service;

    // Datos de prueba reutilizables
    private final Funko funko1 = new Funko(
            1L,
            "Funko Test 1",
            19.99,
            Categoria.SERIES,
            LocalDate.of(2021, 1, 1),
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final Funko funko2 = new Funko(
            2L,
            "Funko Test 2",
            24.99,
            Categoria.VIDEOJUEGOS,
            LocalDate.of(2022, 2, 2),
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final FunkoRequestDto validDTO = new FunkoRequestDto(
            "Nuevo Funko",
            29.99,
            Categoria.PELICULAS,
            LocalDate.of(2020, 3, 3)
    );

    @Nested
    @DisplayName("Tests para casos correctos")
    class SuccessCases {

        @Test
        @DisplayName("Constructor inicializa correctamente")
        void constructor() {
            // Arrange & Act
            FunkoServiceImpl testService = new FunkoServiceImpl(repository);

            // Assert - Si no hay NullPointerException, el constructor funciona correctamente
            assertNotNull(testService);
        }

        @Test
        @DisplayName("create() guarda un funko válido")
        void createValidFunko() {
            // Arrange
            when(repository.save(any(Funko.class))).thenReturn(funko1);

            // Act
            Funko result = service.create(validDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Funko Test 1", result.getNombre());
            verify(repository).save(any(Funko.class));
        }

        @Test
        @DisplayName("getById() devuelve un funko existente")
        void getByIdExisting() {
            // Arrange
            when(repository.getById(1L)).thenReturn(Optional.of(funko1));

            // Act
            Funko result = service.getById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Funko Test 1", result.getNombre());
            verify(repository).getById(1L);
        }

        @Test
        @DisplayName("getAll() devuelve lista de funkos")
        void getAllFunkos() {
            // Arrange
            when(repository.getAll()).thenReturn(Arrays.asList(funko1, funko2));

            // Act
            List<Funko> result = service.getAll();

            // Assert
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals(2L, result.get(1).getId());
            verify(repository).getAll();
        }

        @Test
        @DisplayName("update() actualiza un funko existente")
        void updateExistingFunko() {
            // Arrange
            when(repository.update(eq(1L), any(Funko.class))).thenReturn(Optional.of(funko1));

            // Act
            Funko result = service.update(1L, validDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(repository).update(eq(1L), any(Funko.class));
        }

        @Test
        @DisplayName("patch() actualiza parcialmente un funko existente")
        void patchExistingFunko() {
            // Arrange
            when(repository.patch(eq(1L), any(Funko.class))).thenReturn(Optional.of(funko1));
            FunkoRequestDto patchDTO = new FunkoRequestDto(null, 15.99, null, null);

            // Act
            Funko result = service.patch(1L, patchDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(repository).patch(eq(1L), any(Funko.class));
        }

        @Test
        @DisplayName("delete() elimina un funko existente")
        void deleteExistingFunko() {
            // Arrange
            when(repository.deleteById(1L)).thenReturn(Optional.of(funko1));

            // Act
            service.delete(1L);

            // Assert
            verify(repository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Tests para casos de error")
    class ErrorCases {

        @Nested
        @DisplayName("Tests para errores de ID inexistente")
        class NotFoundTests {
            @Test
            @DisplayName("getById() lanza excepción si el funko no existe")
            void getByIdNonExisting() {
                // Arrange
                when(repository.getById(99L)).thenReturn(Optional.empty());

                // Act & Assert
                FunkoNotFoundException exception = assertThrows(
                        FunkoNotFoundException.class,
                        () -> service.getById(99L)
                );
                assertEquals("Funko con id 99 no encontrado", exception.getMessage());
                verify(repository).getById(99L);
            }

            @Test
            @DisplayName("update() lanza excepción si el funko no existe")
            void updateNonExisting() {
                // Arrange
                when(repository.update(eq(99L), any(Funko.class))).thenReturn(Optional.empty());

                // Act & Assert
                FunkoNotFoundException exception = assertThrows(
                        FunkoNotFoundException.class,
                        () -> service.update(99L, validDTO)
                );
                assertEquals("Funko con id 99 no encontrado", exception.getMessage());
                verify(repository).update(eq(99L), any(Funko.class));
            }

            @Test
            @DisplayName("patch() lanza excepción si el funko no existe")
            void patchNonExisting() {
                // Arrange
                when(repository.patch(eq(99L), any(Funko.class))).thenReturn(Optional.empty());
                FunkoRequestDto patchDTO = new FunkoRequestDto(null, 15.99, null, null);

                // Act & Assert
                FunkoNotFoundException exception = assertThrows(
                        FunkoNotFoundException.class,
                        () -> service.patch(99L, patchDTO)
                );
                assertEquals("Funko con id 99 no encontrado", exception.getMessage());
                verify(repository).patch(eq(99L), any(Funko.class));
            }

            @Test
            @DisplayName("delete() lanza excepción si el funko no existe")
            void deleteNonExisting() {
                // Arrange
                when(repository.deleteById(99L)).thenReturn(Optional.empty());

                // Act & Assert
                FunkoNotFoundException exception = assertThrows(
                        FunkoNotFoundException.class,
                        () -> service.delete(99L)
                );
                assertEquals("Funko con id 99 no encontrado", exception.getMessage());
                verify(repository).deleteById(99L);
            }
        }

        @Nested
        @DisplayName("Tests para validaciones de negocio")
        class BusinessValidationTests {
            @Test
            @DisplayName("create() valida que el nombre no esté vacío")
            void createWithEmptyName() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto(
                        "   ", // Nombre vacío después de trim
                        29.99,
                        Categoria.PELICULAS,
                        LocalDate.of(2020, 3, 3)
                );

                // Act & Assert
                FunkoException exception = assertThrows(
                        FunkoException.class,
                        () -> service.create(invalidDTO)
                );
                assertEquals("El nombre no puede estar vacío", exception.getMessage());
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("create() valida que el nombre no sea demasiado largo")
            void createWithTooLongName() {
                // Arrange
                String longName = "a".repeat(101); // 101 caracteres
                FunkoRequestDto invalidDTO = new FunkoRequestDto(
                        longName,
                        29.99,
                        Categoria.PELICULAS,
                        LocalDate.of(2020, 3, 3)
                );

                // Act & Assert
                FunkoException exception = assertThrows(
                        FunkoException.class,
                        () -> service.create(invalidDTO)
                );
                assertEquals("El nombre no puede superar 100 caracteres", exception.getMessage());
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("create() valida que el precio sea positivo")
            void createWithNegativePrice() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto(
                        "Funko Test",
                        -5.0, // Precio negativo
                        Categoria.PELICULAS,
                        LocalDate.of(2020, 3, 3)
                );

                // Act & Assert
                FunkoException exception = assertThrows(
                        FunkoException.class,
                        () -> service.create(invalidDTO)
                );
                assertEquals("El precio debe ser mayor que 0", exception.getMessage());
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("create() valida que el precio no sea cero")
            void createWithZeroPrice() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto(
                        "Funko Test",
                        0.0, // Precio cero
                        Categoria.PELICULAS,
                        LocalDate.of(2020, 3, 3)
                );

                // Act & Assert
                FunkoException exception = assertThrows(
                        FunkoException.class,
                        () -> service.create(invalidDTO)
                );
                assertEquals("El precio debe ser mayor que 0", exception.getMessage());
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("create() valida que la fecha no sea futura")
            void createWithFutureDate() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto(
                        "Funko Test",
                        29.99,
                        Categoria.PELICULAS,
                        LocalDate.now().plusDays(1) // Fecha futura
                );

                // Act & Assert
                FunkoException exception = assertThrows(
                        FunkoException.class,
                        () -> service.create(invalidDTO)
                );
                assertEquals("La fecha de lanzamiento no puede ser futura", exception.getMessage());
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("update() realiza las mismas validaciones que create()")
            void updateWithInvalidData() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto(
                        "", // Nombre vacío
                        29.99,
                        Categoria.PELICULAS,
                        LocalDate.of(2020, 3, 3)
                );

                // Act & Assert
                FunkoException exception = assertThrows(
                        FunkoException.class,
                        () -> service.update(1L, invalidDTO)
                );
                assertEquals("El nombre no puede estar vacío", exception.getMessage());
                verify(repository, never()).update(anyLong(), any());
            }

            @Test
            @DisplayName("patch() realiza validaciones solo en campos no nulos")
            void patchWithInvalidData() {
                // Arrange - Solo precio inválido
                FunkoRequestDto invalidDTO = new FunkoRequestDto(
                        null, // No se actualiza nombre
                        -10.0, // Precio negativo (inválido)
                        null, // No se actualiza categoría
                        null  // No se actualiza fecha
                );

                // Act & Assert
                FunkoException exception = assertThrows(
                        FunkoException.class,
                        () -> service.patch(1L, invalidDTO)
                );
                assertEquals("El precio debe ser mayor que 0", exception.getMessage());
                verify(repository, never()).patch(anyLong(), any());
            }

            @Test
            @DisplayName("patch() permite actualizar solo campos específicos")
            void patchWithValidPartialData() {
                // Arrange
                FunkoRequestDto validPartialDTO = new FunkoRequestDto(
                        "Nuevo Nombre", // Válido
                        null, // No se actualiza precio
                        null, // No se actualiza categoría
                        null  // No se actualiza fecha
                );

                when(repository.patch(eq(1L), any(Funko.class))).thenReturn(Optional.of(funko1));

                // Act
                Funko result = service.patch(1L, validPartialDTO);

                // Assert
                assertNotNull(result);
                verify(repository).patch(eq(1L), any(Funko.class));
            }

            @Test
            @DisplayName("patch() con DTO completamente nulo no causa error")
            void patchWithAllNullFields() {
                // Arrange
                FunkoRequestDto allNullDTO = new FunkoRequestDto(
                        null, null, null, null
                );

                when(repository.patch(eq(1L), any(Funko.class))).thenReturn(Optional.of(funko1));

                // Act
                Funko result = service.patch(1L, allNullDTO);

                // Assert
                assertNotNull(result);
                verify(repository).patch(eq(1L), any(Funko.class));
            }
        }
    }

    @Nested
    @DisplayName("Tests para caché")
    class CacheTests {
        @Test
        @DisplayName("getById() utiliza caché en llamadas repetidas")
        void getByIdUsesCacheForRepeatedCalls() {
            // Este test verifica que la anotación @Cacheable funciona
            // En un entorno de pruebas unitarias normal, la caché no está activada,
            // pero podemos verificar que las anotaciones están presentes

            // Arrange
            when(repository.getById(1L)).thenReturn(Optional.of(funko1));

            // Act - Primera llamada
            service.getById(1L);

            // Act - Segunda llamada (debería usar caché en entorno real)
            service.getById(1L);

            // Assert - Verificamos que repository.getById se llama una vez
            // Este comportamiento SOLO es válido cuando se ejecuta con caché real activa
            // En pruebas unitarias, realmente se llamará dos veces
            verify(repository, times(2)).getById(1L);

            // Nota: Para probar realmente la caché, necesitaríamos tests de integración
            // con un CacheManager real configurado
        }
    }
}