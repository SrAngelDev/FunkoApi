package srangeldev.funkoapi.Funko.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
// ¡¡IMPORTANTE!! Añadir esta importación
import org.springframework.test.annotation.DirtiesContext;
import srangeldev.funkoapi.Categoria.models.Categoria;
import srangeldev.funkoapi.Funko.models.Funko;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración para la capa de Repositorio de Funko.
 * Usamos @DataJpaTest para configurar un contexto de Spring con una BBDD en memoria.
 */
@DataJpaTest
// Esta anotación deshabilita la ejecución automática de data.sql
(properties = {"spring.sql.init.mode=never"})
class FunkoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Para preparar los datos (Arrange)

    @Autowired
    private FunkoRepository funkoRepository; // El repositorio que vamos a probar (Act)

    // Datos de prueba
    private Categoria cat1;
    private Categoria cat2;
    private Funko funko1;
    private Funko funko2;
    private Funko funko3;

    @BeforeEach
    void setUp() {
        // Creamos y persistimos las categorías primero
        cat1 = new Categoria();
        cat1.setNombre("SERIES");
        entityManager.persist(cat1);

        cat2 = new Categoria();
        cat2.setNombre("PELICULAS");
        entityManager.persist(cat2);

        // Creamos los Funkos
        funko1 = new Funko();
        funko1.setNombre("Batman"); // Contiene "man"
        funko1.setPrecio(20.00);    // < 26.00
        funko1.setCategoria(cat1);
        funko1.setFechaLanzamiento(LocalDate.now().minusYears(1));

        funko2 = new Funko();
        funko2.setNombre("Joker");  // NO contiene "man"
        funko2.setPrecio(30.00);    // NO < 26.00
        funko2.setCategoria(cat1);
        funko2.setFechaLanzamiento(LocalDate.now());

        funko3 = new Funko();
        funko3.setNombre("Spiderman"); // Contiene "man"
        funko3.setPrecio(25.00);     // < 26.00
        funko3.setCategoria(cat2);
        funko3.setFechaLanzamiento(LocalDate.now().minusMonths(6));

        // Persistimos los Funkos
        entityManager.persist(funko1);
        entityManager.persist(funko2);
        entityManager.persist(funko3);

        // Sincronizamos con la BBDD
        entityManager.flush();
    }

    @Test
    @DisplayName("Debe encontrar Funkos por nombre (ignorando mayúsculas)")
    void findByNombreContainingIgnoreCase() {
        // Arrange (hecho en setUp)

        // Act
        List<Funko> result = funkoRepository.findByNombreContainingIgnoreCase("man");

        // Assert
        // Con los datos de setUp ("Batman", "Joker", "Spiderman"), solo 2 deben coincidir.
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(f -> f.getNombre().equals("Batman")));
        assertTrue(result.stream().anyMatch(f -> f.getNombre().equals("Spiderman")));

        // Act (probando case-insensitive)
        List<Funko> resultLower = funkoRepository.findByNombreContainingIgnoreCase("joker");
        assertEquals(1, resultLower.size());
        assertEquals("Joker", resultLower.get(0).getNombre());

        // Act (sin resultados)
        List<Funko> resultNone = funkoRepository.findByNombreContainingIgnoreCase("thanos");
        assertTrue(resultNone.isEmpty());
    }

    @Test
    @DisplayName("Debe encontrar Funkos con precio anterior a (menor que)")
    void findByPrecioBefore() {
        // Arrange (Precios: 20, 30, 25)

        // Act
        List<Funko> result = funkoRepository.findByPrecioBefore(26.00);

        // Assert
        // Con los datos de setUp (20.0, 30.0, 25.0), solo 2 deben coincidir
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(f -> f.getNombre().equals("Batman")));
        assertTrue(result.stream().anyMatch(f -> f.getNombre().equals("Spiderman")));

        // Act (borde: no debe incluir el precio exacto)
        List<Funko> resultEdge = funkoRepository.findByPrecioBefore(20.00);
        assertTrue(resultEdge.isEmpty());
    }

    @Test
    @DisplayName("Debe encontrar Funkos por objeto Categoria")
    void findByCategoria() {
        // Arrange (hecho en setUp)

        // Act
        List<Funko> resultCat1 = funkoRepository.findByCategoria(cat1);
        List<Funko> resultCat2 = funkoRepository.findByCategoria(cat2);

        // Assert
        assertEquals(2, resultCat1.size());
        assertTrue(resultCat1.stream().allMatch(f -> f.getCategoria().getNombre().equals("SERIES")));

        assertEquals(1, resultCat2.size());
        assertEquals("Spiderman", resultCat2.get(0).getNombre());
    }

    @Test
    @DisplayName("Debe encontrar una lista de Funkos por UUID")
    void findByUuid() {
        // Arrange
        UUID uuidBuscado = funko1.getUuid();

        // Act
        List<Funko> result = funkoRepository.findByUuid(uuidBuscado);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Batman", result.get(0).getNombre());

        // Act (UUID no existente)
        List<Funko> resultNone = funkoRepository.findByUuid(UUID.randomUUID());
        assertTrue(resultNone.isEmpty());
    }

    @Test
    @DisplayName("Debe comprobar si existe un Funko por ID de Categoria")
    void existsByCategoria_Id() {
        // Arrange (hecho en setUp)
        Long idCat1 = cat1.getId();
        Long idCat2 = cat2.getId();
        Long idInexistente = 999L;

        // Act & Assert
        assertTrue(funkoRepository.existsByCategoria_Id(idCat1));
        assertTrue(funkoRepository.existsByCategoria_Id(idCat2));
        assertFalse(funkoRepository.existsByCategoria_Id(idInexistente));
    }
}

