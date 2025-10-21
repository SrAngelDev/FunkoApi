package srangeldev.funkoapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import srangeldev.funkoapi.dto.FunkoRequestDto;
import srangeldev.funkoapi.dto.FunkoResponseDto;
import srangeldev.funkoapi.exceptions.FunkoException;
import srangeldev.funkoapi.exceptions.FunkoNotFoundException;
import srangeldev.funkoapi.exceptions.GlobalExceptionHandler;
import srangeldev.funkoapi.mappers.FunkoMapper;
import srangeldev.funkoapi.models.Funko;
import srangeldev.funkoapi.models.enums.Categoria;
import srangeldev.funkoapi.services.FunkoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FunkoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FunkoService funkoService;

    @Mock
    private FunkoMapper mapper;

    @InjectMocks
    private FunkoController controller;

    private ObjectMapper objectMapper;

    // Datos de prueba
    private final Funko funko1 = new Funko(
            1L,
            "Funko 1",
            19.99,
            Categoria.SERIES,
            LocalDate.of(2020, 1, 1),
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final Funko funko2 = new Funko(
            2L,
            "Funko 2",
            29.99,
            Categoria.PELICULAS,
            LocalDate.of(2021, 2, 2),
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final FunkoResponseDto responseDTO1 = new FunkoResponseDto(
            1L,
            "Funko 1",
            19.99,
            Categoria.SERIES,
            LocalDate.of(2020, 1, 1),
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final FunkoResponseDto responseDTO2 = new FunkoResponseDto(
            2L,
            "Funko 2",
            29.99,
            Categoria.PELICULAS,
            LocalDate.of(2021, 2, 2),
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final FunkoRequestDto createDTO = new FunkoRequestDto(
            "Nuevo Funko",
            39.99,
            Categoria.VIDEOJUEGOS,
            LocalDate.of(2022, 3, 3)
    );

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Para serializar/deserializar fechas

        // Configuración global de excepciones
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("Tests para casos correctos")
    class SuccessCases {

        @Test
        @DisplayName("getAll() devuelve lista de funkos")
        void getAllFunkos() throws Exception {
            // Arrange
            List<Funko> funkos = Arrays.asList(funko1, funko2);
            List<FunkoResponseDto> responseDTOs = Arrays.asList(responseDTO1, responseDTO2);

            when(funkoService.getAll()).thenReturn(funkos);
            when(mapper.toResponse(funko1)).thenReturn(responseDTO1);
            when(mapper.toResponse(funko2)).thenReturn(responseDTO2);

            // Act & Assert
            mockMvc.perform(get("/funkos"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].nombre", is("Funko 1")))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].nombre", is("Funko 2")));

            verify(funkoService).getAll();
        }

        @Test
        @DisplayName("getById() devuelve un funko existente")
        void getByIdExisting() throws Exception {
            // Arrange
            when(funkoService.getById(1L)).thenReturn(funko1);
            when(mapper.toResponse(funko1)).thenReturn(responseDTO1);

            // Act & Assert
            mockMvc.perform(get("/funkos/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nombre", is("Funko 1")))
                    .andExpect(jsonPath("$.precio", is(19.99)))
                    .andExpect(jsonPath("$.categoria", is("SERIES")));

            verify(funkoService).getById(1L);
            verify(mapper).toResponse(funko1);
        }

        @Test
        @DisplayName("create() crea un nuevo funko")
        void createFunko() throws Exception {
            // Arrange
            when(funkoService.create(any(FunkoRequestDto.class))).thenReturn(funko1);
            when(mapper.toResponse(funko1)).thenReturn(responseDTO1);

            // Act & Assert
            mockMvc.perform(post("/funkos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/funkos/1"))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nombre", is("Funko 1")));

            verify(funkoService).create(any(FunkoRequestDto.class));
            verify(mapper).toResponse(funko1);
        }

        @Test
        @DisplayName("update() actualiza un funko existente")
        void updateExistingFunko() throws Exception {
            // Arrange
            when(funkoService.update(eq(1L), any(FunkoRequestDto.class))).thenReturn(funko1);
            when(mapper.toResponse(funko1)).thenReturn(responseDTO1);

            // Act & Assert
            mockMvc.perform(put("/funkos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nombre", is("Funko 1")));

            verify(funkoService).update(eq(1L), any(FunkoRequestDto.class));
            verify(mapper).toResponse(funko1);
        }

        @Test
        @DisplayName("patch() actualiza parcialmente un funko existente")
        void patchExistingFunko() throws Exception {
            // Arrange
            FunkoRequestDto patchDTO = new FunkoRequestDto(null, 15.99, null, null);

            when(funkoService.patch(eq(1L), any(FunkoRequestDto.class))).thenReturn(funko1);
            when(mapper.toResponse(funko1)).thenReturn(responseDTO1);

            // Act & Assert
            mockMvc.perform(patch("/funkos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nombre", is("Funko 1")));

            verify(funkoService).patch(eq(1L), any(FunkoRequestDto.class));
            verify(mapper).toResponse(funko1);
        }

        @Test
        @DisplayName("delete() elimina un funko existente")
        void deleteExistingFunko() throws Exception {
            // Act & Assert
            mockMvc.perform(delete("/funkos/1"))
                    .andExpect(status().isNoContent());

            verify(funkoService).delete(1L);
        }

        @Test
        @DisplayName("getAll() usando la ruta vacía funciona correctamente")
        void getAllWithEmptyPath() throws Exception {
            // Arrange
            List<Funko> funkos = Arrays.asList(funko1, funko2);
            List<FunkoResponseDto> responseDTOs = Arrays.asList(responseDTO1, responseDTO2);

            when(funkoService.getAll()).thenReturn(funkos);
            when(mapper.toResponse(funko1)).thenReturn(responseDTO1);
            when(mapper.toResponse(funko2)).thenReturn(responseDTO2);

            // Act & Assert - probar la ruta vacía
            mockMvc.perform(get("/funkos/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)));

            verify(funkoService).getAll();
        }
    }

    @Nested
    @DisplayName("Tests para casos de error")
    class ErrorCases {

        @Test
        @DisplayName("getById() devuelve 404 cuando el funko no existe")
        void getByIdNonExisting() throws Exception {
            // Arrange
            when(funkoService.getById(99L)).thenThrow(new FunkoNotFoundException(99L));

            // Act & Assert
            mockMvc.perform(get("/funkos/99"))
                    .andExpect(status().isNotFound());

            verify(funkoService).getById(99L);
        }

        @Test
        @DisplayName("create() devuelve 400 con datos inválidos")
        void createInvalidData() throws Exception {
            // Arrange - DTO con campos inválidos
            FunkoRequestDto invalidDTO = new FunkoRequestDto(
                    "", // Nombre vacío
                    -10.0, // Precio negativo
                    null, // Categoría null
                    null  // Fecha null
            );

            // Act & Assert
            mockMvc.perform(post("/funkos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("create() maneja errores de negocio")
        void createBusinessError() throws Exception {
            // Arrange
            when(funkoService.create(any(FunkoRequestDto.class)))
                    .thenThrow(new FunkoException("Error de negocio"));

            // Act & Assert
            mockMvc.perform(post("/funkos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Error de negocio")));

            verify(funkoService).create(any(FunkoRequestDto.class));
        }

        @Test
        @DisplayName("update() devuelve 404 cuando el funko no existe")
        void updateNonExisting() throws Exception {
            // Arrange
            when(funkoService.update(eq(99L), any(FunkoRequestDto.class)))
                    .thenThrow(new FunkoNotFoundException(99L));

            // Act & Assert
            mockMvc.perform(put("/funkos/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isNotFound());

            verify(funkoService).update(eq(99L), any(FunkoRequestDto.class));
        }

        @Test
        @DisplayName("update() devuelve 400 con datos inválidos")
        void updateInvalidData() throws Exception {
            // Arrange - DTO con campos inválidos
            FunkoRequestDto invalidDTO = new FunkoRequestDto(
                    "", // Nombre vacío
                    -10.0, // Precio negativo
                    null, // Categoría null
                    null  // Fecha null
            );

            // Act & Assert
            mockMvc.perform(put("/funkos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("patch() devuelve 404 cuando el funko no existe")
        void patchNonExisting() throws Exception {
            // Arrange
            FunkoRequestDto patchDTO = new FunkoRequestDto(null, 15.99, null, null);

            when(funkoService.patch(eq(99L), any(FunkoRequestDto.class)))
                    .thenThrow(new FunkoNotFoundException(99L));

            // Act & Assert
            mockMvc.perform(patch("/funkos/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchDTO)))
                    .andExpect(status().isNotFound());

            verify(funkoService).patch(eq(99L), any(FunkoRequestDto.class));
        }

        @Test
        @DisplayName("patch() maneja errores de negocio")
        void patchBusinessError() throws Exception {
            // Arrange
            FunkoRequestDto patchDTO = new FunkoRequestDto(null, -15.99, null, null); // Precio inválido

            when(funkoService.patch(eq(1L), any(FunkoRequestDto.class)))
                    .thenThrow(new FunkoException("El precio debe ser mayor que 0"));

            // Act & Assert
            mockMvc.perform(patch("/funkos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("El precio debe ser mayor que 0")));

            verify(funkoService).patch(eq(1L), any(FunkoRequestDto.class));
        }

        @Test
        @DisplayName("delete() devuelve 404 cuando el funko no existe")
        void deleteNonExisting() throws Exception {
            // Arrange
            doThrow(new FunkoNotFoundException(99L)).when(funkoService).delete(99L);

            // Act & Assert
            mockMvc.perform(delete("/funkos/99"))
                    .andExpect(status().isNotFound());

            verify(funkoService).delete(99L);
        }
    }
}