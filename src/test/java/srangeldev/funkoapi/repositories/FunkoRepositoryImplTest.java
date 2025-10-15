package srangeldev.funkoapi.repositories;

import org.junit.jupiter.api.Test;
import srangeldev.funkoapi.models.Funko;
import srangeldev.funkoapi.models.enums.Categoria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FunkoRepositoryImplTest {

    @Test
    void init() {
        // isForTest=false por defecto: no debe precargar nada
        FunkoRepositoryImpl repoFalse = new FunkoRepositoryImpl();
        repoFalse.init();
        assertTrue(repoFalse.getAll().isEmpty(), "Con isForTest=false no debe haber datos precargados");

        // isForTest=true: debe precargar 3 elementos
        FunkoRepositoryImpl repoTrue = new FunkoRepositoryImpl();
        repoTrue.isForTest = true; // mismo paquete -> acceso permitido (campo protected)
        repoTrue.init();
        List<Funko> all = repoTrue.getAll();
        assertEquals(3, all.size(), "Con isForTest=true deben cargarse 3 Funkos de ejemplo");
        // Comprobamos que tienen id asignado
        assertNotNull(all.get(0).getId());
        assertNotNull(all.get(1).getId());
        assertNotNull(all.get(2).getId());
    }

    @Test
    void getAll() {
        FunkoRepositoryImpl repo = new FunkoRepositoryImpl();
        // Vacío inicialmente
        assertTrue(repo.getAll().isEmpty());

        // Insertamos varios y comprobamos orden por id
        Funko f1 = new Funko(null, "A", 10.0, Categoria.VIDEOJUEGOS, LocalDate.of(2020,1,1), null, null);
        Funko f2 = new Funko(null, "B", 12.0, Categoria.COMICS, LocalDate.of(2021,2,2), null, null);
        Funko f3 = new Funko(null, "C", 14.0, Categoria.PELICULAS, LocalDate.of(2022,3,3), null, null);
        repo.save(f1);
        repo.save(f2);
        repo.save(f3);

        List<Funko> all = repo.getAll();
        assertEquals(3, all.size());
        assertEquals(1L, all.get(0).getId());
        assertEquals(2L, all.get(1).getId());
        assertEquals(3L, all.get(2).getId());
    }

    @Test
    void getById() {
        FunkoRepositoryImpl repo = new FunkoRepositoryImpl();
        Funko saved = repo.save(new Funko(null, "Item", 9.99, Categoria.SERIES, LocalDate.of(2019,9,9), null, null));

        Optional<Funko> found = repo.getById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Item", found.get().getNombre());

        assertTrue(repo.getById(999L).isEmpty(), "Id inexistente debe devolver Optional.empty()");
    }

    @Test
    void save() {
        FunkoRepositoryImpl repo = new FunkoRepositoryImpl();
        Funko f1 = repo.save(new Funko(null, "X", 5.0, Categoria.OTROS, LocalDate.of(2018,8,8), null, null));
        Funko f2 = repo.save(new Funko(null, "Y", 6.0, Categoria.MUSICA, LocalDate.of(2017,7,7), null, null));

        assertNotNull(f1.getId());
        assertNotNull(f1.getCreatedAt());
        assertNotNull(f1.getUpdatedAt());
        assertEquals(1L, f1.getId());
        assertEquals(2L, f2.getId());
    }

    @Test
    void update() {
        FunkoRepositoryImpl repo = new FunkoRepositoryImpl();
        Funko original = repo.save(new Funko(null, "Old", 10.0, Categoria.VIDEOJUEGOS, LocalDate.of(2020,1,1), null, null));
        var createdAtOriginal = original.getCreatedAt();
        var updatedAtOriginal = original.getUpdatedAt();

        Funko cambios = new Funko(null, "New", 20.0, Categoria.COMICS, LocalDate.of(2021,2,2), null, null);
        Optional<Funko> updatedOpt = repo.update(original.getId(), cambios);
        assertTrue(updatedOpt.isPresent());
        Funko updated = updatedOpt.get();
        assertEquals("New", updated.getNombre());
        assertEquals(20.0, updated.getPrecio());
        assertEquals(Categoria.COMICS, updated.getCategoria());
        assertEquals(LocalDate.of(2021,2,2), updated.getFechaLanzamiento());
        // createdAt no cambia; updatedAt debe diferir del original
        assertEquals(createdAtOriginal, updated.getCreatedAt());
        assertNotEquals(updatedAtOriginal, updated.getUpdatedAt());

        // actualizar un id inexistente -> empty
        assertTrue(repo.update(999L, cambios).isEmpty());
    }

    @Test
    void patch() {
        FunkoRepositoryImpl repo = new FunkoRepositoryImpl();
        Funko original = repo.save(new Funko(null, "Nombre", 10.0, Categoria.SERIES, LocalDate.of(2020,1,1), null, null));
        var createdAtOriginal = original.getCreatedAt();

        // Sólo cambiamos el precio; resto null para mantener valores
        Funko cambiosParciales = new Funko(null, null, 15.5, null, null, null, null);
        Optional<Funko> patchedOpt = repo.patch(original.getId(), cambiosParciales);
        assertTrue(patchedOpt.isPresent());
        Funko patched = patchedOpt.get();
        assertEquals("Nombre", patched.getNombre()); // sin cambio
        assertEquals(15.5, patched.getPrecio()); // cambiado
        assertEquals(Categoria.SERIES, patched.getCategoria()); // sin cambio
        assertEquals(LocalDate.of(2020,1,1), patched.getFechaLanzamiento()); // sin cambio
        assertEquals(createdAtOriginal, patched.getCreatedAt()); // createdAt no cambia
        assertNotNull(patched.getUpdatedAt());

        // patch a id inexistente -> empty
        assertTrue(repo.patch(999L, cambiosParciales).isEmpty());
    }

    @Test
    void deleteById() {
        FunkoRepositoryImpl repo = new FunkoRepositoryImpl();
        Funko saved = repo.save(new Funko(null, "Del", 11.0, Categoria.DEPORTES, LocalDate.of(2016,6,6), null, null));
        Optional<Funko> deleted = repo.deleteById(saved.getId());
        assertTrue(deleted.isPresent());
        assertEquals("Del", deleted.get().getNombre());
        // Ya no debe existir
        assertTrue(repo.getById(saved.getId()).isEmpty());

        // Eliminar inexistente
        assertTrue(repo.deleteById(999L).isEmpty());
    }
}