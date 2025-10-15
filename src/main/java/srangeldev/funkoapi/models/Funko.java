package srangeldev.funkoapi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import srangeldev.funkoapi.models.enums.Categoria;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio simple para representar un Funko.
 * No usamos JPA ni base de datos: se almacenará en memoria en el servicio.
 */
@Data
public class Funko {
    // Atributos públicos (del dominio)
    private Long id;                    // Identificador único
    private String nombre;              // Nombre del Funko
    private Double precio;              // Precio
    private Categoria categoria;        // Categoría
    private LocalDate fechaLanzamiento; // Fecha de lanzamiento

    // Atributos internos (metadatos)
    private LocalDateTime createdAt;    // Fecha/hora de creación
    private LocalDateTime updatedAt;    // Fecha/hora de última actualización

    public Funko() {}

    public Funko(Long id, String nombre, Double precio, Categoria categoria, LocalDate fechaLanzamiento,
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
