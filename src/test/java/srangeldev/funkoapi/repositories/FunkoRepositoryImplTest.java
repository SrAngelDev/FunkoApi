package srangeldev.funkoapi.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import srangeldev.funkoapi.models.Funko;
import srangeldev.funkoapi.models.enums.Categoria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FunkoRepositoryImplTest {

    private FunkoRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new FunkoRepositoryImpl();
    }

    @Nested
    @DisplayName("Tests de inicialización")
    class InitTests {
        @Test
        @DisplayName("init() sin datos de prueba")
        void initNoTestData() {
            // Arrange - isForTest=false por defecto
            // Act
            repository.init();

            // Assert
            assertTrue(repository.getAll().isEmpty(),
                    "Con isForTest=false no debe haber datos precargados");
        }

        @Test
        @DisplayName("init() con datos de prueba")
        void initWithTestData() {
            // Arrange
            repository.isForTest = true; // mismo paquete -> acceso permitido (campo protected)

            // Act
            repository.init();
            List<Funko> all = repository.getAll();

            // Assert
            assertEquals(3, all.size(),
                    "Con isForTest=true deben cargarse 3 Funkos de ejemplo");
            // Comprobamos que tienen id asignado
            assertNotNull(all.get(0).getId(), "El primer Funko debe tener ID asignado");
            assertNotNull(all.get(1).getId(), "El segundo Funko debe tener ID asignado");
            assertNotNull(all.get(2).getId(), "El tercer Funko debe tener ID asignado");
        }
    }

    @Nested
    @DisplayName("Tests de consulta")
    class QueryTests {
        @Test
        @DisplayName("getAll() devuelve lista vacía inicialmente")
        void getAllEmpty() {
            // Act & Assert
            assertTrue(repository.getAll().isEmpty(), "Inicialmente la lista debe estar vacía");
        }

        @Test
        @DisplayName("getAll() devuelve todos los elementos ordenados por ID")
        void getAllWithElements() {
            // Arrange
            Funko f1 = new Funko(null, "A", 10.0, Categoria.VIDEOJUEGOS, LocalDate.of(2020,1,1), null, null);
            Funko f2 = new Funko(null, "B", 12.0, Categoria.COMICS, LocalDate.of(2021,2,2), null, null);
            Funko f3 = new Funko(null, "C", 14.0, Categoria.PELICULAS, LocalDate.of(2022,3,3), null, null);
            repository.save(f1);
            repository.save(f2);
            repository.save(f3);

            // Act
            List<Funko> all = repository.getAll();

            // Assert
            assertEquals(3, all.size(), "Debe haber 3 elementos en la lista");
            assertEquals(1L, all.get(0).getId(), "El primer elemento debe tener ID 1");
            assertEquals(2L, all.get(1).getId(), "El segundo elemento debe tener ID 2");
            assertEquals(3L, all.get(2).getId(), "El tercer elemento debe tener ID 3");
        }

        @Test
        @DisplayName("getById() devuelve Funko si existe")
        void getByIdExisting() {
            // Arrange
            Funko saved = repository.save(new Funko(null, "Item", 9.99, Categoria.SERIES,
                    LocalDate.of(2019,9,9), null, null));

            // Act
            Optional<Funko> found = repository.getById(saved.getId());

            // Assert
            assertTrue(found.isPresent(), "El Funko debe encontrarse");
            assertEquals("Item", found.get().getNombre(), "El nombre debe coincidir");
            assertEquals(9.99, found.get().getPrecio(), "El precio debe coincidir");
            assertEquals(Categoria.SERIES, found.get().getCategoria(), "La categoría debe coincidir");
        }

        @Test
        @DisplayName("getById() devuelve Optional.empty() si no existe")
        void getByIdNonExisting() {
            // Act & Assert
            assertTrue(repository.getById(999L).isEmpty(),
                    "Id inexistente debe devolver Optional.empty()");
        }
    }

    @Nested
    @DisplayName("Tests de inserción")
    class InsertionTests {
        @Test
        @DisplayName("save() asigna ID y timestamps correctamente")
        void saveAssignsIdAndTimestamps() {
            // Arrange
            Funko funko = new Funko(null, "X", 5.0, Categoria.OTROS,
                    LocalDate.of(2018,8,8), null, null);

            // Act
            Funko saved = repository.save(funko);

            // Assert
            assertNotNull(saved.getId(), "Debe asignar un ID");
            assertNotNull(saved.getCreatedAt(), "Debe asignar createdAt");
            assertNotNull(saved.getUpdatedAt(), "Debe asignar updatedAt");
            assertEquals(1L, saved.getId(), "El primer ID debe ser 1");
        }

        @Test
        @DisplayName("save() incrementa IDs secuencialmente")
        void saveIncrementsId() {
            // Arrange & Act
            Funko f1 = repository.save(new Funko(null, "X", 5.0, Categoria.OTROS,
                    LocalDate.of(2018,8,8), null, null));
            Funko f2 = repository.save(new Funko(null, "Y", 6.0, Categoria.MUSICA,
                    LocalDate.of(2017,7,7), null, null));

            // Assert
            assertEquals(1L, f1.getId(), "El primer Funko debe tener ID 1");
            assertEquals(2L, f2.getId(), "El segundo Funko debe tener ID 2");
        }

        @Test
        @DisplayName("save() siempre asigna un nuevo ID secuencial, incluso si ya tiene ID")
        void saveWithExistingId() {
            // Arrange
            Funko funkoConId = new Funko(100L, "PreAsignado", 25.0, Categoria.COMICS,
                    LocalDate.of(2023,3,3), null, null);

            // Act
            Funko saved = repository.save(funkoConId);

            // Assert
            assertEquals(1L, saved.getId(),
                    "Debe asignar un nuevo ID secuencial (1) independientemente del ID previo");
            assertNotNull(saved.getCreatedAt(), "Debe asignar createdAt");
            assertNotNull(saved.getUpdatedAt(), "Debe asignar updatedAt");
        }
    }

    @Nested
    @DisplayName("Tests de actualización")
    class UpdateTests {
        @Test
        @DisplayName("update() modifica todos los campos correctamente")
        void updateSuccess() {
            // Arrange
            Funko original = repository.save(new Funko(null, "Old", 10.0, Categoria.VIDEOJUEGOS,
                    LocalDate.of(2020,1,1), null, null));
            LocalDateTime createdAtOriginal = original.getCreatedAt();
            LocalDateTime updatedAtOriginal = original.getUpdatedAt();

            // Esperar un momento para asegurar que updatedAt será diferente
            try { Thread.sleep(10); } catch (InterruptedException e) { }

            Funko cambios = new Funko(null, "New", 20.0, Categoria.COMICS,
                    LocalDate.of(2021,2,2), null, null);

            // Act
            Optional<Funko> updatedOpt = repository.update(original.getId(), cambios);

            // Assert
            assertTrue(updatedOpt.isPresent(), "Debe devolver el Funko actualizado");
            Funko updated = updatedOpt.get();
            assertEquals("New", updated.getNombre(), "El nombre debe actualizarse");
            assertEquals(20.0, updated.getPrecio(), "El precio debe actualizarse");
            assertEquals(Categoria.COMICS, updated.getCategoria(), "La categoría debe actualizarse");
            assertEquals(LocalDate.of(2021,2,2), updated.getFechaLanzamiento(),
                    "La fecha debe actualizarse");

            // createdAt no cambia; updatedAt debe diferir del original
            assertEquals(createdAtOriginal, updated.getCreatedAt(),
                    "createdAt no debe cambiar");
            assertNotEquals(updatedAtOriginal, updated.getUpdatedAt(),
                    "updatedAt debe actualizarse");
        }

        @Test
        @DisplayName("update() devuelve Optional.empty() para ID inexistente")
        void updateNonExisting() {
            // Arrange
            Funko cambios = new Funko(null, "New", 20.0, Categoria.COMICS,
                    LocalDate.of(2021,2,2), null, null);

            // Act
            Optional<Funko> result = repository.update(999L, cambios);

            // Assert
            assertTrue(result.isEmpty(), "Debe devolver Optional.empty() para ID inexistente");
        }

        @Test
        @DisplayName("patch() actualiza solo los campos no nulos")
        void patchPartialUpdate() {
            // Arrange
            Funko original = repository.save(new Funko(null, "Nombre", 10.0, Categoria.SERIES,
                    LocalDate.of(2020,1,1), null, null));
            LocalDateTime createdAtOriginal = original.getCreatedAt();

            // Esperar un momento para asegurar que updatedAt será diferente
            try { Thread.sleep(10); } catch (InterruptedException e) { }

            // Solo cambiamos el precio; resto null para mantener valores
            Funko cambiosParciales = new Funko(null, null, 15.5, null, null, null, null);

            // Act
            Optional<Funko> patchedOpt = repository.patch(original.getId(), cambiosParciales);

            // Assert
            assertTrue(patchedOpt.isPresent(), "Debe devolver el Funko actualizado");
            Funko patched = patchedOpt.get();
            assertEquals("Nombre", patched.getNombre(), "El nombre no debe cambiar");
            assertEquals(15.5, patched.getPrecio(), "El precio debe actualizarse");
            assertEquals(Categoria.SERIES, patched.getCategoria(), "La categoría no debe cambiar");
            assertEquals(LocalDate.of(2020,1,1), patched.getFechaLanzamiento(),
                    "La fecha no debe cambiar");
            assertEquals(createdAtOriginal, patched.getCreatedAt(), "createdAt no debe cambiar");
            assertNotNull(patched.getUpdatedAt(), "updatedAt debe existir");
            assertTrue(patched.getUpdatedAt().isAfter(createdAtOriginal),
                    "updatedAt debe ser posterior a createdAt");
        }

        @Test
        @DisplayName("patch() con todos los campos nulos no modifica los datos")
        void patchAllFieldsNull() {
            // Arrange
            Funko original = repository.save(new Funko(null, "Original", 10.0, Categoria.SERIES,
                    LocalDate.of(2020,1,1), null, null));
            String nombreOriginal = original.getNombre();
            double precioOriginal = original.getPrecio();
            Categoria categoriaOriginal = original.getCategoria();
            LocalDate fechaOriginal = original.getFechaLanzamiento();

            // Todos los campos son null
            Funko cambiosTodosNulos = new Funko(null, null, null, null, null, null, null);

            // Act
            Optional<Funko> patchedOpt = repository.patch(original.getId(), cambiosTodosNulos);

            // Assert
            assertTrue(patchedOpt.isPresent(), "Debe devolver el Funko");
            Funko patched = patchedOpt.get();
            assertEquals(nombreOriginal, patched.getNombre(), "El nombre debe permanecer igual");
            assertEquals(precioOriginal, patched.getPrecio(), "El precio debe permanecer igual");
            assertEquals(categoriaOriginal, patched.getCategoria(),
                    "La categoría debe permanecer igual");
            assertEquals(fechaOriginal, patched.getFechaLanzamiento(),
                    "La fecha debe permanecer igual");
        }

        @Test
        @DisplayName("patch() devuelve Optional.empty() para ID inexistente")
        void patchNonExisting() {
            // Arrange
            Funko cambiosParciales = new Funko(null, null, 15.5, null, null, null, null);

            // Act
            Optional<Funko> result = repository.patch(999L, cambiosParciales);

            // Assert
            assertTrue(result.isEmpty(),
                    "Debe devolver Optional.empty() para ID inexistente");
        }
    }

    @Nested
    @DisplayName("Tests de eliminación")
    class DeletionTests {
        @Test
        @DisplayName("deleteById() elimina y devuelve el Funko si existe")
        void deleteExisting() {
            // Arrange
            Funko saved = repository.save(new Funko(null, "Del", 11.0, Categoria.DEPORTES,
                    LocalDate.of(2016,6,6), null, null));

            // Act
            Optional<Funko> deleted = repository.deleteById(saved.getId());

            // Assert
            assertTrue(deleted.isPresent(), "Debe devolver el Funko eliminado");
            assertEquals("Del", deleted.get().getNombre(), "El nombre debe coincidir");
            // Ya no debe existir
            assertTrue(repository.getById(saved.getId()).isEmpty(),
                    "El Funko no debe existir después de eliminarlo");
        }

        @Test
        @DisplayName("deleteById() devuelve Optional.empty() si no existe")
        void deleteNonExisting() {
            // Act
            Optional<Funko> result = repository.deleteById(999L);

            // Assert
            assertTrue(result.isEmpty(),
                    "Debe devolver Optional.empty() para ID inexistente");
        }

        @Test
        @DisplayName("deleteById() mantiene la secuencia de ID después de eliminar")
        void deletePreservesIdSequence() {
            // Arrange
            Funko f1 = repository.save(new Funko(null, "A", 10.0, Categoria.VIDEOJUEGOS,
                    LocalDate.of(2020,1,1), null, null));
            Funko f2 = repository.save(new Funko(null, "B", 12.0, Categoria.COMICS,
                    LocalDate.of(2021,2,2), null, null));

            // Act
            repository.deleteById(f1.getId()); // Eliminar el primer Funko
            Funko f3 = repository.save(new Funko(null, "C", 14.0, Categoria.PELICULAS,
                    LocalDate.of(2022,3,3), null, null));

            // Assert
            assertEquals(3L, f3.getId(),
                    "El nuevo Funko debe tener ID 3, no reutilizar el ID 1");
        }
    }

    @Nested
    @DisplayName("Tests de casos borde")
    class EdgeCaseTests {
        @Test
        @DisplayName("getAll() devuelve lista inmutable o copia")
        void getAllReturnsSafeList() {
            // Arrange
            repository.save(new Funko(null, "A", 10.0, Categoria.VIDEOJUEGOS,
                    LocalDate.of(2020,1,1), null, null));

            // Act
            List<Funko> list1 = repository.getAll();
            List<Funko> list2 = repository.getAll();

            // Assert
            // Verificamos que son objetos diferentes (copia o inmutable)
            assertNotSame(list1, list2,
                    "getAll() debe devolver una nueva lista en cada llamada");

            assertEquals(list1.size(), list2.size(),
                    "Las listas deben tener el mismo contenido");
        }
    }
}