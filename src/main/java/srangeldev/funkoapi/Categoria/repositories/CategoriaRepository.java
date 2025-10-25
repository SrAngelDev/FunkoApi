package srangeldev.funkoapi.Categoria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import srangeldev.funkoapi.Categoria.models.Categoria;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}