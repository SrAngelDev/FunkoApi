package srangeldev.funkoapi.Funko.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder // Usamos Builder para facilitar la construcción en el mapper
@NoArgsConstructor
@AllArgsConstructor
public class FunkoResponseDto {
    private Long id;
    private UUID uuid;
    private String nombre;
    private Double precio;
    private CategoriaResponseDto categoria;
    private LocalDate fechaLanzamiento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}