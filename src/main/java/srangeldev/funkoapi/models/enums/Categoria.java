package srangeldev.funkoapi.models.enums;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import srangeldev.funkoapi.models.Funko;

import java.util.List;

/**
 * Categorías posibles para un Funko.
 */
@Entity
@Table(name = "categorias")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    //Relacion bidireccional ya que una categoria puede tener muchos Funkos
    @OneToMany(mappedBy = "categoria")
    private final List<Funko> funkos;

    public Categoria() {
        this.funkos = null;
    }
}
