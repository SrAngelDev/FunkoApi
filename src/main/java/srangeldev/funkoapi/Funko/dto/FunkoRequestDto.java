package srangeldev.funkoapi.Funko.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de entrada para crear/actualizar un Funko.
 * Adaptado para usar campos en inglés y recibir solo el ID de la categoría.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunkoRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive
    private Double precio;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @PastOrPresent
    private LocalDate fechaLanzamiento;
}