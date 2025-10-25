package srangeldev.funkoapi.Funko.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String name;

    @Column(nullable=false)
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Categoria category;

    @Column(nullable=false, name = "release_date")
    private LocalDate releaseDate;

    @CreatedDate
    @Column(nullable=false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable=false, name = "updated_at")
    private LocalDateTime updatedAt;
}
