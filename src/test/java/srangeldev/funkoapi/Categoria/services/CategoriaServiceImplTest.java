package srangeldev.funkoapi.Categoria.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import srangeldev.funkoapi.Categoria.dto.CategoriaRequestDto;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaException;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaNotFoundException;
import srangeldev.funkoapi.Categoria.mappers.CategoriaMapper;
import srangeldev.funkoapi.Categoria.models.Categoria;
import srangeldev.funkoapi.Categoria.repositories.CategoriaRepository;
import srangeldev.funkoapi.Funko.repositories.FunkoRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private FunkoRepository funkoRepository;
    @Mock
    private CategoriaMapper mapper;

    @InjectMocks
    private CategoriaServiceImpl service;

    // --- Datos de prueba ---
    private Categoria cat1;
    private Categoria cat2;
    private CategoriaResponseDto dto1;
    private CategoriaResponseDto dto2;
    private CategoriaRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Entidades
        cat1 = new Categoria(1L, "SERIES", LocalDateTime.now(), LocalDateTime.now());
        cat2 = new Categoria(2L, "PELICULAS", LocalDateTime.now(), LocalDateTime.now());

        // DTOs de Respuesta
        dto1 = new CategoriaResponseDto(1L, "SERIES", cat1.getCreatedAt(), cat1.getUpdatedAt());
        dto2 = new CategoriaResponseDto(2L, "PELICULAS", cat2.getCreatedAt(), cat2.getUpdatedAt());

        // DTO de Petición
        requestDto = new CategoriaRequestDto("SERIES");
    }
    // --- Fin de datos ---


    @Nested
    @DisplayName("Tests para casos correctos (Success)")
    class SuccessCases {

        @Test
        @DisplayName("getAll() devuelve lista de DTOs")
        void getAll() {
            // Arrange
            when(categoriaRepository.findAll()).thenReturn(List.of(cat1, cat2));
            when(mapper.toResponseDto(cat1)).thenReturn(dto1);
            when(mapper.toResponseDto(cat2)).thenReturn(dto2);

            // Act
            List<CategoriaResponseDto> result = service.getAll();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("SERIES", result.get(0).getNombre());
            verify(categoriaRepository).findAll();
            verify(mapper, times(2)).toResponseDto(any(Categoria.class));
        }

        @Test
        @DisplayName("getAll() devuelve lista vacía si no hay categorías")
        void getAllEmpty() {
            // Arrange
            when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<CategoriaResponseDto> result = service.getAll();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(categoriaRepository).findAll();
            verify(mapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("getById() devuelve DTO si existe")
        void getById() {
            // Arrange
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat1));
            when(mapper.toResponseDto(cat1)).thenReturn(dto1);

            // Act
            CategoriaResponseDto result = service.getById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("SERIES", result.getNombre());
            verify(categoriaRepository).findById(1L);
            verify(mapper).toResponseDto(cat1);
        }

        @Test
        @DisplayName("create() guarda y devuelve DTO si el nombre es único")
        void create() {
            // Arrange
            Categoria newCat = new Categoria(null, "SERIES", null, null);
            when(categoriaRepository.findByNombre("SERIES")).thenReturn(Optional.empty());
            when(mapper.toCategoria(requestDto)).thenReturn(newCat);
            when(categoriaRepository.save(newCat)).thenReturn(cat1); // Devuelve la entidad guardada con ID
            when(mapper.toResponseDto(cat1)).thenReturn(dto1);

            // Act
            CategoriaResponseDto result = service.create(requestDto);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("SERIES", result.getNombre());
            verify(categoriaRepository).findByNombre("SERIES");
            verify(mapper).toCategoria(requestDto);
            verify(categoriaRepository).save(newCat);
            verify(mapper).toResponseDto(cat1);
        }

        @Test
        @DisplayName("update() actualiza y devuelve DTO si existe")
        void update() {
            // Arrange
            CategoriaRequestDto updateRequest = new CategoriaRequestDto("SERIES V2");
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat1));
            when(categoriaRepository.findByNombre("SERIES V2")).thenReturn(Optional.empty());
            when(categoriaRepository.save(cat1)).thenReturn(cat1);
            when(mapper.toResponseDto(cat1)).thenReturn(dto1); // Asumimos que dto1 se actualiza

            // Act
            CategoriaResponseDto result = service.update(1L, updateRequest);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(categoriaRepository).findById(1L);
            verify(categoriaRepository).findByNombre("SERIES V2");
            verify(categoriaRepository).save(cat1);
            verify(mapper).toResponseDto(cat1);
        }

        @Test
        @DisplayName("update() funciona si el nombre ya existe pero es de la misma entidad")
        void updateWithSameName() {
            // Arrange
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat1));
            when(categoriaRepository.findByNombre("SERIES")).thenReturn(Optional.of(cat1)); // El nombre existe, pero en ID=1
            when(categoriaRepository.save(cat1)).thenReturn(cat1);
            when(mapper.toResponseDto(cat1)).thenReturn(dto1);

            // Act
            CategoriaResponseDto result = service.update(1L, requestDto); // requestDto tiene "SERIES"

            // Assert
            assertNotNull(result);
            verify(categoriaRepository).findById(1L);
            verify(categoriaRepository).findByNombre("SERIES");
            verify(categoriaRepository).save(cat1);
        }

        @Test
        @DisplayName("delete() elimina si existe y no tiene Funkos")
        void delete() {
            // Arrange
            when(categoriaRepository.existsById(1L)).thenReturn(true);
            when(funkoRepository.existsByCategoria_Id(1L)).thenReturn(false); // No hay Funkos
            doNothing().when(categoriaRepository).deleteById(1L);

            // Act
            service.delete(1L);

            // Assert
            verify(categoriaRepository).existsById(1L);
            verify(funkoRepository).existsByCategoria_Id(1L);
            verify(categoriaRepository).deleteById(1L);
        }
    }


    @Nested
    @DisplayName("Tests para errores 'Not Found'")
    class NotFoundErrors {

        @Test
        @DisplayName("getById() lanza CategoriaNotFoundException si no existe")
        void getByIdNotFound() {
            // Arrange
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            var exception = assertThrows(CategoriaNotFoundException.class, () -> {
                service.getById(99L);
            });
            assertEquals("No se pudo encontrar la categoría con el ID: 99", exception.getMessage());
            verify(categoriaRepository).findById(99L);
            verify(mapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("update() lanza CategoriaNotFoundException si no existe")
        void updateNotFound() {
            // Arrange
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            var exception = assertThrows(CategoriaNotFoundException.class, () -> {
                service.update(99L, requestDto);
            });
            assertEquals("No se pudo encontrar la categoría con el ID: 99", exception.getMessage());
            verify(categoriaRepository).findById(99L);
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("delete() lanza CategoriaNotFoundException si no existe")
        void deleteNotFound() {
            // Arrange
            when(categoriaRepository.existsById(99L)).thenReturn(false);

            // Act & Assert
            var exception = assertThrows(CategoriaNotFoundException.class, () -> {
                service.delete(99L);
            });
            assertEquals("No se pudo encontrar la categoría con el ID: 99", exception.getMessage());
            verify(categoriaRepository).existsById(99L);
            verify(funkoRepository, never()).existsByCategoria_Id(any());
            verify(categoriaRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Tests para errores de Lógica de Negocio (CategoriaException)")
    class BusinessLogicErrors {

        @Test
        @DisplayName("create() lanza CategoriaException si el nombre ya existe")
        void createNameExists() {
            // Arrange
            when(categoriaRepository.findByNombre("SERIES")).thenReturn(Optional.of(cat1));

            // Act & Assert
            var exception = assertThrows(CategoriaException.class, () -> {
                service.create(requestDto);
            });
            assertEquals("El nombre 'SERIES' ya existe.", exception.getMessage());
            verify(categoriaRepository).findByNombre("SERIES");
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("update() lanza CategoriaException si el nombre ya existe en OTRA categoría")
        void updateNameExistsOnOther() {
            // Arrange
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat1));
            when(categoriaRepository.findByNombre("PELICULAS")).thenReturn(Optional.of(cat2)); // cat2 tiene ID 2L

            CategoriaRequestDto updateRequest = new CategoriaRequestDto("PELICULAS");

            // Act & Assert
            var exception = assertThrows(CategoriaException.class, () -> {
                service.update(1L, updateRequest);
            });
            assertEquals("El nombre 'PELICULAS' ya existe.", exception.getMessage());
            verify(categoriaRepository).findById(1L);
            verify(categoriaRepository).findByNombre("PELICULAS");
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("delete() lanza CategoriaException si tiene Funkos asociados")
        void deleteFailsIfFunkosExist() {
            // Arrange
            when(categoriaRepository.existsById(1L)).thenReturn(true);
            when(funkoRepository.existsByCategoria_Id(1L)).thenReturn(true); // ¡Hay Funkos!

            // Act & Assert
            var exception = assertThrows(CategoriaException.class, () -> {
                service.delete(1L);
            });
            assertEquals("No se puede borrar la categoría. Tiene Funkos asociados.", exception.getMessage());
            verify(categoriaRepository).existsById(1L);
            verify(funkoRepository).existsByCategoria_Id(1L);
            verify(categoriaRepository, never()).deleteById(any());
        }
    }
}
