package srangeldev.funkoapi.dto;

import lombok.Data;
import srangeldev.funkoapi.models.enums.Categoria;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de salida para devolver información del Funko al cliente.
 */
@Data
public class FunkoResponseDto {
    private Long id;
    private String nombre;
    private Double precio;
    private Categoria categoria;
    private LocalDate fechaLanzamiento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FunkoResponseDto(Long id, String nombre, Double precio, Categoria categoria, LocalDate fechaLanzamiento,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.fechaLanzamiento = fechaLanzamiento;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
