package srangeldev.funkoapi.Funko.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import srangeldev.funkoapi.Categoria.models.Categoria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio simple para representar un Funko.
 * Usamos JPA para almacenarlo en BBDD
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Funko {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable=false)
    private String nombre;

    @Column(nullable=false)
    private Double precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable=false, name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    @CreatedDate
    @Column(nullable=false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable=false, name = "updated_at")
    private LocalDateTime updatedAt;
}
