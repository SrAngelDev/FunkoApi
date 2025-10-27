package srangeldev.funkoapi.Categoria.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import srangeldev.funkoapi.Categoria.dto.CategoriaRequestDto;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaNotFoundException; // Asumo que esta excepción existe
import srangeldev.funkoapi.Categoria.services.CategoriaService;
import srangeldev.funkoapi.expections.GlobalExceptionHandler; // Asumo que este manejador global existe

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoriaService service;

    @InjectMocks
    private CategoriaController controller;

    private ObjectMapper objectMapper;

    // --- Datos de prueba ---
    private final CategoriaResponseDto catResponse1 = CategoriaResponseDto.builder()
            .id(1L)
            .nombre("SERIES")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final CategoriaResponseDto catResponse2 = CategoriaResponseDto.builder()
            .id(2L)
            .nombre("PELICULAS")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final CategoriaRequestDto catRequest = CategoriaRequestDto.builder()
            .nombre("SERIES")
            .build();
    // --- Fin de datos ---


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Configuramos MockMvc en modo standalone
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                // Añadimos el manejador de validación interno del controlador
                .setControllerAdvice(new CategoriaController(service))
                // Añadimos el manejador global para otras excepciones (como NotFound)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("Tests para casos correctos (Success)")
    class SuccessCases {

        @Test
        @DisplayName("getAllCategorias() devuelve lista de categorías")
        void getAllCategorias() throws Exception {
            // Arrange
            when(service.getAll()).thenReturn(List.of(catResponse1, catResponse2));

            // Act & Assert
            mockMvc.perform(get("/api/categorias")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].nombre", is("SERIES")));

            verify(service).getAll();
        }

        @Test
        @DisplayName("getAllCategorias() devuelve lista vacía si no hay categorías")
        void getAllCategoriasEmpty() throws Exception {
            // Arrange
            when(service.getAll()).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/api/categorias")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(service).getAll();
        }

        @Test
        @DisplayName("getCategoriaById() devuelve una categoría existente")
        void getCategoriaById() throws Exception {
            // Arrange
            when(service.getById(1L)).thenReturn(catResponse1);

            // Act & Assert
            mockMvc.perform(get("/api/categorias/1")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nombre", is("SERIES")));

            verify(service).getById(1L);
        }

        @Test
        @DisplayName("createCategoria() crea una nueva categoría")
        void createCategoria() throws Exception {
            // Arrange
            when(service.create(any(CategoriaRequestDto.class))).thenReturn(catResponse1);

            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(catRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nombre", is("SERIES")));

            verify(service).create(any(CategoriaRequestDto.class));
        }

        @Test
        @DisplayName("updateCategoria() actualiza una categoría existente")
        void updateCategoria() throws Exception {
            // Arrange
            when(service.update(eq(1L), any(CategoriaRequestDto.class))).thenReturn(catResponse1);

            // Act & Assert
            mockMvc.perform(put("/api/categorias/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(catRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nombre", is("SERIES")));

            verify(service).update(eq(1L), any(CategoriaRequestDto.class));
        }

        @Test
        @DisplayName("deleteCategoria() elimina una categoría existente")
        void deleteCategoria() throws Exception {
            // Arrange
            doNothing().when(service).delete(1L);

            // Act & Assert
            mockMvc.perform(delete("/api/categorias/1"))
                    .andExpect(status().isNoContent());

            verify(service).delete(1L);
        }
    }

    @Nested
    @DisplayName("Tests para casos de error (Failure)")
    class ErrorCases {

        @Test
        @DisplayName("getCategoriaById() devuelve 404 si no existe")
        void getCategoriaByIdNotFound() throws Exception {
            // Arrange
            when(service.getById(99L)).thenThrow(new CategoriaNotFoundException(99L));

            // Act & Assert
            mockMvc.perform(get("/api/categorias/99")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", containsString("No se pudo encontrar la categoría con el ID: 99")));

            verify(service).getById(99L);
        }

        @Test
        @DisplayName("updateCategoria() devuelve 404 si no existe")
        void updateCategoriaNotFound() throws Exception {
            // Arrange
            when(service.update(eq(99L), any(CategoriaRequestDto.class)))
                    .thenThrow(new CategoriaNotFoundException(99L));

            // Act & Assert
            mockMvc.perform(put("/api/categorias/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(catRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", containsString("No se pudo encontrar la categoría con el ID: 99")));

            verify(service).update(eq(99L), any(CategoriaRequestDto.class));
        }

        @Test
        @DisplayName("deleteCategoria() devuelve 404 si no existe")
        void deleteCategoriaNotFound() throws Exception {
            // Arrange
            doThrow(new CategoriaNotFoundException(99L)).when(service).delete(99L);

            // Act & Assert
            mockMvc.perform(delete("/api/categorias/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error", containsString("No se pudo encontrar la categoría con el ID: 99")));

            verify(service).delete(99L);
        }

        @Test
        @DisplayName("createCategoria() devuelve 400 por validación (nombre vacío)")
        void createCategoriaValidationEmpty() throws Exception {
            // Arrange
            CategoriaRequestDto invalidRequest = new CategoriaRequestDto(" "); // Vacío

            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    // Esto prueba tu manejador handleValidationExceptions
                    .andExpect(jsonPath("$.nombre", is("El nombre no puede estar vacío")));

            verify(service, never()).create(any());
        }

        @Test
        @DisplayName("createCategoria() devuelve 400 por validación (nombre nulo)")
        void createCategoriaValidationNull() throws Exception {
            // Arrange
            CategoriaRequestDto invalidRequest = new CategoriaRequestDto(null); // Nulo

            // Act & Assert
            mockMvc.perform(post("/api/categorias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.nombre", is("El nombre no puede estar vacío")));

            verify(service, never()).create(any());
        }

        @Test
        @DisplayName("updateCategoria() devuelve 400 por validación (nombre vacío)")
        void updateCategoriaValidation() throws Exception {
            // Arrange
            CategoriaRequestDto invalidRequest = new CategoriaRequestDto(" "); // Vacío

            // Act & Assert
            mockMvc.perform(put("/api/categorias/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.nombre", is("El nombre no puede estar vacío")));

            verify(service, never()).update(anyLong(), any());
        }
    }
}
