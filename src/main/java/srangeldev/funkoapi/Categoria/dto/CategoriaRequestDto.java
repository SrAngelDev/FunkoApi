package srangeldev.funkoapi.Categoria.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequestDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
}