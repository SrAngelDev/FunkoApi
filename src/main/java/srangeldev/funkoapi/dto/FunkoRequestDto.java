package srangeldev.funkoapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import srangeldev.funkoapi.models.enums.Categoria;

import java.time.LocalDate;

/**
 * DTO de entrada para crear/actualizar un Funko.
 * Incluye validaciones básicas con Jakarta Validation.
 */
@Data
public class FunkoRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que 0")
    private Double precio;

    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private LocalDate fechaLanzamiento;

    public FunkoRequestDto(String nombre, Double precio, Categoria categoria, LocalDate fechaLanzamiento) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.fechaLanzamiento = fechaLanzamiento;
    }
}
