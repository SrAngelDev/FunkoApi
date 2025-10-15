package srangeldev.funkoapi.repositories;

import srangeldev.funkoapi.models.Funko;

import java.util.List;
import java.util.Optional;

/**
 * Capa de repositorio (en memoria) por debajo del servicio.
 * Define las operaciones CRUD que el servicio utilizará.
 */
public interface FunkoRepository {
    List<Funko> getAll();
    Optional<Funko> getById(Long id);
    Funko save(Funko funko);
    Optional<Funko> update(Long id, Funko funko);
    Optional<Funko> patch(Long id, Funko funko);
    Optional<Funko> deleteById(Long id);
}
