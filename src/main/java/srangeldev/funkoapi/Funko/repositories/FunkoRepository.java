package srangeldev.funkoapi.Funko.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import srangeldev.funkoapi.Categoria.models.Categoria;
import srangeldev.funkoapi.Funko.models.Funko;
import java.util.List;
import java.util.UUID;

/**
 * Capa de repositorio (en memoria) por debajo del servicio.
 * Define las operaciones CRUD que el servicio utilizará.
 */
@Repository
public interface FunkoRepository extends JpaRepository<Funko, Long> {
    List<Funko> findByNombreContainingIgnoreCase(String nombre);

    List<Funko> findByPrecioBefore(Double precioBefore);

    List<Funko> findByCategoria(Categoria categoria);

    List<Funko> findByUuid(UUID uuid);
}
