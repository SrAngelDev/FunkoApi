package srangeldev.funkoapi.notifications.dto;

import lombok.Data;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FunkoNotificationDto {
    private Long id;
    private String uuid;
    private String nombre;
    private Double precio;
    private String categoria;
    private String fechaLanzamiento;
    private String createdAt;
    private String updatedAt;
}
