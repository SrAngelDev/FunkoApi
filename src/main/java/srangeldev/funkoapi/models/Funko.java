package srangeldev.funkoapi.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import srangeldev.funkoapi.models.enums.Categoria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio simple para representar un Funko.
 * Usamos JPA para almacenarlo en BBDD
 */
@Data
@Entity
@Table(name = "funkos")
public class Funko {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String nombre;

    private Double precio;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    private LocalDate fechaLanzamiento;

    // Atributos internos (metadatos)
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

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
