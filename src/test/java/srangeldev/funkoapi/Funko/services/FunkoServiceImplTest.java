package srangeldev.funkoapi.Funko.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import srangeldev.funkoapi.Categoria.models.Categoria;
import srangeldev.funkoapi.Categoria.repositories.CategoriaRepository;
import srangeldev.funkoapi.Funko.dto.FunkoRequestDto;
import srangeldev.funkoapi.Funko.exceptions.FunkoException;
import srangeldev.funkoapi.Funko.exceptions.FunkoNotFoundException;
import srangeldev.funkoapi.Funko.models.Funko;
import srangeldev.funkoapi.Funko.repositories.FunkoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {

    @Mock
    private FunkoRepository funkoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private FunkoServiceImpl funkoService;

    // --- Datos de prueba reutilizables ---
    private final Categoria categoria1 = new Categoria(
            1L,
            "SERIES",
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final Categoria categoria2 = new Categoria(
            2L,
            "PELICULAS",
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final Funko funko1 = new Funko(
            1L,
            UUID.randomUUID(),
            "Funko Test 1",
            19.99,
            categoria1,
            LocalDate.of(2021, 1, 1),
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final Funko funko2 = new Funko(
            2L,
            UUID.randomUUID(),
            "Funko Test 2",
            24.99,
            categoria2,
            LocalDate.of(2022, 2, 2),
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final FunkoRequestDto validDTO = new FunkoRequestDto(
            "Nuevo Funko",
            29.99,
            1L, // Corresponde a categoria1
            LocalDate.of(2020, 3, 3)
    );
    // --- Fin de datos de prueba ---


    @Nested
    @DisplayName("Tests para casos correctos")
    class SuccessCases {

        @Test
        @DisplayName("create() guarda un funko válido")
        void createValidFunko() {
            // Arrange
            // 1. Simular la búsqueda de categoría
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria1));
            // 2. Simular el guardado del funko
            // Usamos ArgumentCaptor para capturar el Funko que se pasa a save()
            ArgumentCaptor<Funko> funkoCaptor = ArgumentCaptor.forClass(Funko.class);
            // Creamos una respuesta simulada
            Funko savedFunko = new Funko(1L, UUID.randomUUID(), validDTO.getNombre(), validDTO.getPrecio(), categoria1, validDTO.getFechaLanzamiento(), LocalDateTime.now(), LocalDateTime.now());
            when(funkoRepository.save(funkoCaptor.capture())).thenReturn(savedFunko);


            // Act
            Funko result = funkoService.create(validDTO);

            // Assert
            assertNotNull(result);
            assertEquals(validDTO.getNombre(), result.getNombre());
            assertEquals(categoria1.getNombre(), result.getCategoria().getNombre());
            // Verificamos que los datos capturados son los esperados
            assertEquals(validDTO.getNombre(), funkoCaptor.getValue().getNombre());
            assertEquals(categoria1, funkoCaptor.getValue().getCategoria());

            verify(categoriaRepository).findById(1L);
            verify(funkoRepository).save(any(Funko.class));
        }

        @Test
        @DisplayName("getById() devuelve un funko existente")
        void getByIdExisting() {
            // Arrange
            when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko1));

            // Act
            Funko result = funkoService.getById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Funko Test 1", result.getNombre());
            verify(funkoRepository).findById(1L);
        }

        @Test
        @DisplayName("getAll() devuelve lista de funkos")
        void getAllFunkos() {
            // Arrange
            when(funkoRepository.findAll()).thenReturn(Arrays.asList(funko1, funko2));

            // Act
            List<Funko> result = funkoService.getAll();

            // Assert
            assertEquals(2, result.size());
            assertEquals(1L, result.get(0).getId());
            assertEquals(2L, result.get(1).getId());
            verify(funkoRepository).findAll();
        }

        @Test
        @DisplayName("update() actualiza un funko existente")
        void updateExistingFunko() {
            // Arrange
            FunkoRequestDto updateDTO = new FunkoRequestDto("Funko Actualizado", 30.0, 2L, LocalDate.of(2022, 2, 2));
            when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko1)); // Devuelve el funko original
            when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria2)); // Devuelve la nueva categoría
            when(funkoRepository.save(any(Funko.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Devuelve el mismo objeto que se le pasa

            // Act
            Funko result = funkoService.update(1L, updateDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId()); // El ID no cambia
            assertEquals("Funko Actualizado", result.getNombre()); // El nombre se actualiza
            assertEquals(categoria2.getNombre(), result.getCategoria().getNombre()); // La categoría se actualiza

            verify(funkoRepository).findById(1L);
            verify(categoriaRepository).findById(2L);
            verify(funkoRepository).save(any(Funko.class));
        }

        @Test
        @DisplayName("patch() actualiza parcialmente un funko existente (solo nombre)")
        void patchExistingFunko_OnlyName() {
            // Arrange
            FunkoRequestDto patchDTO = new FunkoRequestDto("Nombre Parcial", null, null, null);
            // Hacemos una copia para evitar modificar el original
            Funko funkoOriginal = new Funko(1L, funko1.getUuid(), funko1.getNombre(), funko1.getPrecio(), funko1.getCategoria(), funko1.getFechaLanzamiento(), funko1.getCreatedAt(), funko1.getUpdatedAt());

            when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoOriginal));
            when(funkoRepository.save(any(Funko.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Funko result = funkoService.patch(1L, patchDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Nombre Parcial", result.getNombre()); // Nombre actualizado
            assertEquals(funko1.getPrecio(), result.getPrecio()); // Precio NO actualizado
            assertEquals(funko1.getCategoria(), result.getCategoria()); // Categoría NO actualizada

            verify(funkoRepository).findById(1L);
            verify(funkoRepository).save(any(Funko.class));
            verify(categoriaRepository, never()).findById(anyLong()); // No se debe llamar a CategoriaRepository
        }

        @Test
        @DisplayName("patch() actualiza parcialmente un funko existente (solo categoria)")
        void patchExistingFunko_OnlyCategory() {
            // Arrange
            FunkoRequestDto patchDTO = new FunkoRequestDto(null, null, 2L, null);
            Funko funkoOriginal = new Funko(1L, funko1.getUuid(), funko1.getNombre(), funko1.getPrecio(), funko1.getCategoria(), funko1.getFechaLanzamiento(), funko1.getCreatedAt(), funko1.getUpdatedAt());

            when(funkoRepository.findById(1L)).thenReturn(Optional.of(funkoOriginal));
            when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria2)); // Simula la nueva categoría
            when(funkoRepository.save(any(Funko.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Funko result = funkoService.patch(1L, patchDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(funko1.getNombre(), result.getNombre()); // Nombre NO actualizado
            assertEquals(categoria2.getNombre(), result.getCategoria().getNombre()); // Categoría actualizada

            verify(funkoRepository).findById(1L);
            verify(categoriaRepository).findById(2L); // Se debe llamar a CategoriaRepository
            verify(funkoRepository).save(any(Funko.class));
        }


        @Test
        @DisplayName("delete() elimina un funko existente")
        void deleteExistingFunko() {
            // Arrange
            when(funkoRepository.existsById(1L)).thenReturn(true);
            // No mockeamos deleteById porque devuelve void, pero podemos usar doNothing()
            doNothing().when(funkoRepository).deleteById(1L);

            // Act
            funkoService.delete(1L);

            // Assert
            verify(funkoRepository).existsById(1L);
            verify(funkoRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Tests para casos de error")
    class ErrorCases {

        @Nested
        @DisplayName("Tests para errores de ID inexistente (FunkoNotFoundException)")
        class NotFoundTests {
            @Test
            @DisplayName("getById() lanza excepción si el funko no existe")
            void getByIdNonExisting() {
                // Arrange
                when(funkoRepository.findById(99L)).thenReturn(Optional.empty());

                // Act & Assert
                FunkoNotFoundException exception = assertThrows(
                        FunkoNotFoundException.class,
                        () -> funkoService.getById(99L)
                );
                assertEquals("Funko con id 99 no encontrado", exception.getMessage());
                verify(funkoRepository).findById(99L);
            }

            @Test
            @DisplayName("update() lanza excepción si el funko no existe")
            void updateNonExisting() {
                // Arrange
                when(funkoRepository.findById(99L)).thenReturn(Optional.empty());

                // Act & Assert
                FunkoNotFoundException exception = assertThrows(
                        FunkoNotFoundException.class,
                        () -> funkoService.update(99L, validDTO)
                );
                assertEquals("Funko con id 99 no encontrado", exception.getMessage());
                verify(funkoRepository).findById(99L);
                verify(funkoRepository, never()).save(any()); // No se debe llamar a save
            }

            @Test
            @DisplayName("patch() lanza excepción si el funko no existe")
            void patchNonExisting() {
                // Arrange
                FunkoRequestDto patchDTO = new FunkoRequestDto(null, 15.99, null, null);
                when(funkoRepository.findById(99L)).thenReturn(Optional.empty());

                // Act & Assert
                FunkoNotFoundException exception = assertThrows(
                        FunkoNotFoundException.class,
                        () -> funkoService.patch(99L, patchDTO)
                );
                assertEquals("Funko con id 99 no encontrado", exception.getMessage());
                verify(funkoRepository).findById(99L);
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("delete() lanza excepción si el funko no existe")
            void deleteNonExisting() {
                // Arrange
                when(funkoRepository.existsById(99L)).thenReturn(false);

                // Act & Assert
                FunkoNotFoundException exception = assertThrows(
                        FunkoNotFoundException.class,
                        () -> funkoService.delete(99L)
                );
                assertEquals("Funko con id 99 no encontrado", exception.getMessage());
                verify(funkoRepository).existsById(99L);
                verify(funkoRepository, never()).deleteById(anyLong()); // No se debe llamar a delete
            }
        }

        @Nested
        @DisplayName("Tests para validaciones de negocio (FunkoException)")
        class BusinessValidationTests {
            @Test
            @DisplayName("create() valida que el nombre no esté vacío")
            void createWithEmptyName() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto("   ", 29.99, 1L, LocalDate.of(2020, 3, 3));

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.create(invalidDTO));
                assertEquals("El nombre no puede estar vacío", exception.getMessage());
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("create() valida que el nombre no sea demasiado largo")
            void createWithTooLongName() {
                // Arrange
                String longName = "a".repeat(101); // 101 caracteres
                FunkoRequestDto invalidDTO = new FunkoRequestDto(longName, 29.99, 1L, LocalDate.of(2020, 3, 3));

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.create(invalidDTO));
                assertEquals("El nombre no puede superar 100 caracteres", exception.getMessage());
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("create() valida que el precio sea positivo (no cero)")
            void createWithZeroPrice() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto("Funko Test", 0.0, 1L, LocalDate.of(2020, 3, 3));

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.create(invalidDTO));
                assertEquals("El precio debe ser mayor que 0", exception.getMessage());
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("create() valida que la fecha no sea futura")
            void createWithFutureDate() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto("Funko Test", 29.99, 1L, LocalDate.now().plusDays(1));

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.create(invalidDTO));
                assertEquals("La fecha de lanzamiento no puede ser futura", exception.getMessage());
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("update() realiza las mismas validaciones (precio negativo)")
            void updateWithInvalidData() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto("Funko Malo", -5.0, 1L, LocalDate.of(2020, 3, 3));

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.update(1L, invalidDTO));
                assertEquals("El precio debe ser mayor que 0", exception.getMessage());
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("patch() realiza validaciones solo en campos no nulos (nombre vacío)")
            void patchWithInvalidData() {
                // Arrange
                FunkoRequestDto invalidDTO = new FunkoRequestDto(" ", null, null, null);

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.patch(1L, invalidDTO));
                assertEquals("El nombre no puede estar vacío", exception.getMessage());
                verify(funkoRepository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("Tests para errores de Categoría (FunkoException)")
        class CategoriaErrorTests {

            @Test
            @DisplayName("create() lanza excepción si Categoria no existe")
            void createWithNonExistingCategoria() {
                // Arrange
                FunkoRequestDto dto = new FunkoRequestDto("Nuevo Funko", 29.99, 99L, LocalDate.of(2020, 3, 3));
                when(categoriaRepository.findById(99L)).thenReturn(Optional.empty()); // Categoria no encontrada

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.create(dto));
                assertEquals("Categoría no encontrada con ID: 99", exception.getMessage());
                verify(categoriaRepository).findById(99L);
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("update() lanza excepción si Categoria no existe")
            void updateWithNonExistingCategoria() {
                // Arrange
                FunkoRequestDto dto = new FunkoRequestDto("Funko Actualizado", 29.99, 99L, LocalDate.of(2020, 3, 3));
                when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko1)); // El Funko sí existe
                when(categoriaRepository.findById(99L)).thenReturn(Optional.empty()); // Pero la nueva Categoria no

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.update(1L, dto));
                assertEquals("Categoría no encontrada con ID: 99", exception.getMessage());
                verify(funkoRepository).findById(1L);
                verify(categoriaRepository).findById(99L);
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("patch() lanza excepción si Categoria no existe")
            void patchWithNonExistingCategoria() {
                // Arrange
                FunkoRequestDto dto = new FunkoRequestDto(null, null, 99L, null); // Solo se actualiza la categoría
                when(funkoRepository.findById(1L)).thenReturn(Optional.of(funko1)); // El Funko sí existe
                when(categoriaRepository.findById(99L)).thenReturn(Optional.empty()); // Pero la nueva Categoria no

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.patch(1L, dto));
                assertEquals("Categoría no encontrada con ID: 99", exception.getMessage());
                verify(funkoRepository).findById(1L);
                verify(categoriaRepository).findById(99L);
                verify(funkoRepository, never()).save(any());
            }

            @Test
            @DisplayName("create() lanza excepción si CategoriaId es nulo")
            void createWithNullCategoriaId() {
                // Arrange
                FunkoRequestDto dto = new FunkoRequestDto("Nuevo Funko", 29.99, null, LocalDate.of(2020, 3, 3));

                // Act & Assert
                FunkoException exception = assertThrows(FunkoException.class, () -> funkoService.create(dto));
                assertEquals("El ID de la categoría no puede ser nulo", exception.getMessage());
                verify(categoriaRepository, never()).findById(any());
                verify(funkoRepository, never()).save(any());
            }
        }
    }
}
