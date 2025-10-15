package srangeldev.funkoapi.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import srangeldev.funkoapi.models.Funko;
import srangeldev.funkoapi.models.enums.Categoria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * Implementación en memoria del repositorio de Funkos, alineada con el estilo "Impl".
 */
@Repository
public class FunkoRepositoryImpl implements FunkoRepository {

    private final Logger log = Logger.getLogger(FunkoRepositoryImpl.class.getName());

    private Long nextId = 0L;
    private final HashMap<Long, Funko> funkos = new HashMap<>();

    // Leemos de application.properties la propiedad funkoapi.isForTest
    @Value("${funkoapi.isForTest:false}")
    protected boolean isForTest;

    // Inicializamos datos de ejemplo sólo si la propiedad está activa
    @PostConstruct
    void init() {
        if (isForTest) {
            log.info("REPOSITORY: Inicializando Funkos de ejemplo para tests");

            save(new Funko(
                    null,
                    "Spider-Man Classic",
                    19.99,
                    Categoria.COMICS,
                    LocalDate.of(2020, 5, 12),
                    null,
                    null
            ));

            save(new Funko(
                    null,
                    "Pikachu Edición Limitada",
                    24.50,
                    Categoria.VIDEOJUEGOS,
                    LocalDate.of(2021, 7, 21),
                    null,
                    null
            ));

            save(new Funko(
                    null,
                    "Freddy Krueger Vintage",
                    18.00,
                    Categoria.PELICULAS,
                    LocalDate.of(2018, 10, 31),
                    null,
                    null
            ));
        }
    }

    @Override
    public List<Funko> getAll() {
        log.info("REPOSITORY: Buscando todos los Funkos");
        return funkos.values().stream()
                .sorted(Comparator.comparing(Funko::getId))
                .toList();
    }

    @Override
    public Optional<Funko> getById(Long id) {
        log.info("REPOSITORY: Buscando el Funko con id: " + id);
        Funko funko = funkos.get(id);
        if (funko == null) {
            log.warning("REPOSITORY: No se ha encontrado el Funko con id: " + id);
            return Optional.empty();
        }
        return Optional.of(funko);
    }

    @Override
    public Funko save(Funko funko) {
        log.info("REPOSITORY: Guardando Funko");
        // Generamos nuevo id
        nextId++;
        // Asignamos id y fechas
        funko.setId(nextId);
        funko.setCreatedAt(LocalDateTime.now());
        funko.setUpdatedAt(LocalDateTime.now());
        // Guardamos
        funkos.put(nextId, funko);
        return funko;
    }

    @Override
    public Optional<Funko> update(Long id, Funko funko) {
        log.info("REPOSITORY: Actualizando el Funko con id: " + id);
        Funko existing = funkos.get(id);
        if (existing == null) {
            log.warning("REPOSITORY: No se ha encontrado el Funko con id: " + id);
            return Optional.empty();
        }
        // Actualizamos campos modificables por el usuario
        existing.setNombre(funko.getNombre());
        existing.setPrecio(funko.getPrecio());
        existing.setCategoria(funko.getCategoria());
        existing.setFechaLanzamiento(funko.getFechaLanzamiento());
        existing.setUpdatedAt(LocalDateTime.now());
        funkos.put(id, existing);
        return Optional.of(existing);
    }

    @Override
    public Optional<Funko> patch(Long id, Funko funko) {
        log.info("REPOSITORY: Haciendo Patch al Funko con id: " + id);
        Funko existing = funkos.get(id);
        if (existing == null) {
            log.warning("REPOSITORY: No se ha encontrado el Funko con id: " + id);
            return Optional.empty();
        }
        if (funko.getNombre() != null) existing.setNombre(funko.getNombre());
        if (funko.getPrecio() != null) existing.setPrecio(funko.getPrecio());
        if (funko.getCategoria() != null) existing.setCategoria(funko.getCategoria());
        if (funko.getFechaLanzamiento() != null) existing.setFechaLanzamiento(funko.getFechaLanzamiento());
        existing.setUpdatedAt(LocalDateTime.now());
        funkos.put(id, existing);
        return Optional.of(existing);
    }

    @Override
    public Optional<Funko> deleteById(Long id) {
        log.info("REPOSITORY: Eliminando el Funko con id: " + id);
        Funko removed = funkos.remove(id);
        if (removed == null) {
            log.warning("REPOSITORY: No se ha encontrado el Funko con id: " + id);
            return Optional.empty();
        }
        return Optional.of(removed);
    }
}
