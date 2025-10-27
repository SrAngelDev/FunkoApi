package srangeldev.funkoapi.Categoria.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import srangeldev.funkoapi.Categoria.models.Categoria;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración para CategoriaRepository.
 * Usamos @DataJpaTest para configurar una BBDD en memoria
 * y probar las consultas personalizadas.
 */
@DataJpaTest(properties = {"spring.sql.init.mode=never"}) // Evita que se ejecute data.sql
class CategoriaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Para preparar los datos (Arrange)

    @Autowired
    private CategoriaRepository categoriaRepository; // El repositorio a probar (Act)

    private Categoria categoria1;
    private Categoria categoria2;

    @BeforeEach
    void setUp() {
        // Arrange: Insertamos datos de prueba frescos antes de cada test
        // Usamos el constructor (id, nombre, createdAt, updatedAt)
        // Id y fechas son null para que JPA los genere.
        categoria1 = new Categoria(null, "SERIES", null, null);
        categoria2 = new Categoria(null, "PELICULAS", null, null);

        entityManager.persistAndFlush(categoria1);
        entityManager.persistAndFlush(categoria2);
    }

    @Test
    @DisplayName("Debe encontrar una categoría por su nombre si existe")
    void findByNombre_ShouldFindExistingCategoria() {
        // Act: Ejecutamos el método del repositorio
        Optional<Categoria> result = categoriaRepository.findByNombre("SERIES");

        // Assert: Verificamos que se encontró la categoría correcta
        assertTrue(result.isPresent());
        assertEquals("SERIES", result.get().getNombre());
        // Comparamos el ID asignado por la BBDD
        assertEquals(categoria1.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Debe devolver Optional vacío si el nombre no existe")
    void findByNombre_ShouldReturnEmptyForNonExistingCategoria() {
        // Act: Buscamos un nombre que no existe
        Optional<Categoria> result = categoriaRepository.findByNombre("INEXISTENTE");

        // Assert: Verificamos que el Optional está vacío
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe ser sensible a mayúsculas/minúsculas (case-sensitive)")
    void findByNombre_ShouldBeCaseSensitive() {
        // Act: Buscamos con un nombre en minúsculas
        Optional<Categoria> result = categoriaRepository.findByNombre("series");

        // Assert: No debería encontrarlo (a menos que la BBDD H2 no sea sensible por defecto)
        // Por defecto, findByNombre es sensible a mayúsculas.
        assertTrue(result.isEmpty());
    }
}
