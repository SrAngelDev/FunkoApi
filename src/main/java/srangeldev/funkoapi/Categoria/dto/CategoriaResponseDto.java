package srangeldev.funkoapi.Categoria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDto {
    private Long id;
    private String nombre;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}