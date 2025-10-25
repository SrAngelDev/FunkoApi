package srangeldev.funkoapi.Funko.dto;

import lombok.Builder;
import lombok.Data;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder // Usamos Builder para facilitar la construcción en el mapper
public class FunkoResponseDto {
    private Long id;
    private UUID uuid;
    private String nombre;
    private Double precio;
    private CategoriaResponseDto categoriaResponseDto;
    private LocalDate fechaLanzamiento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}