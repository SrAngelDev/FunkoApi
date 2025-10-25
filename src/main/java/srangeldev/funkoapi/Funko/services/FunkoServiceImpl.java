package srangeldev.funkoapi.Funko.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import srangeldev.funkoapi.Categoria.models.Categoria; 
import srangeldev.funkoapi.Categoria.repositories.CategoriaRepository; 
import srangeldev.funkoapi.Funko.exceptions.FunkoException;
import srangeldev.funkoapi.Funko.dto.FunkoRequestDto;
import srangeldev.funkoapi.Funko.exceptions.FunkoNotFoundException;
import srangeldev.funkoapi.Funko.models.Funko;
import srangeldev.funkoapi.Funko.repositories.FunkoRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@CacheConfig(cacheNames = "funkos")
public class FunkoServiceImpl implements FunkoService {

    private final FunkoRepository repository;
    private final CategoriaRepository categoriaRepository; 

    @Autowired
    public FunkoServiceImpl(FunkoRepository repository, CategoriaRepository categoriaRepository) { 
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @CachePut(key = "#result.id")
    public Funko create(FunkoRequestDto dto) {
        // Validación de reglas de negocio
        validarNegocio(dto);

        // Buscamos la categoría. Asumimos que el DTO trae getCategoriaId()
        Categoria categoria = findCategoriaById(dto.getCategoriaId());

        // Creamos el Funko usando setters
        Funko funko = new Funko();
        funko.setNombre(dto.getNombre()); 
        funko.setPrecio(dto.getPrecio()); 
        funko.setCategoria(categoria); 
        funko.setFechaLanzamiento(dto.getFechaLanzamiento());

        // uuid se autogenera en el modelo.
        // createdAt y updatedAt se gestionan por @CreatedDate y @LastModifiedDate (Auditoría)

        return repository.save(funko);
    }

    @Override
    @Cacheable(key = "#id")
    public Funko getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new FunkoNotFoundException(id));
    }

    @Override
    public List<Funko> getAll() {
        return repository.findAll();
    }

    @Override
    @CachePut(key = "#id")
    @Transactional
    public Funko update(Long id, FunkoRequestDto dto) {
        validarNegocio(dto);

        Funko funkoExistente = repository.findById(id).orElseThrow(() -> new FunkoNotFoundException(id));

        // Buscamos la nueva categoría
        Categoria categoria = findCategoriaById(dto.getCategoriaId());

        // Actualizamos campos
        funkoExistente.setNombre(dto.getNombre()); 
        funkoExistente.setPrecio(dto.getPrecio());
        funkoExistente.setCategoria(categoria); 
        funkoExistente.setFechaLanzamiento(dto.getFechaLanzamiento()); 

        // updatedAt se actualiza automáticamente por @LastModifiedDate
        return repository.save(funkoExistente);
    }

    @Override
    @CachePut(key = "#id")
    @Transactional
    public Funko patch(Long id, FunkoRequestDto dto) {
        validarNegocio(dto);

        Funko funkoExistente = repository.findById(id).orElseThrow(() -> new FunkoNotFoundException(id));

        // Aplicamos cambios parciales (solo si el campo no es nulo en el DTO)
        if (dto.getNombre() != null) {
            funkoExistente.setNombre(dto.getNombre());
        }
        if (dto.getPrecio() != null) {
            funkoExistente.setPrecio(dto.getPrecio());
        }
        if (dto.getCategoriaId() != null) {
            // Solo buscamos y actualizamos la categoría si se provee en el DTO
            Categoria categoria = findCategoriaById(dto.getCategoriaId());
            funkoExistente.setCategoria(categoria);
        }
        if (dto.getFechaLanzamiento() != null) {
            funkoExistente.setFechaLanzamiento(dto.getFechaLanzamiento());
        }

        // updatedAt se actualiza automáticamente
        return repository.save(funkoExistente);
    }

    @Override
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        // Primero comprobamos que existe para lanzar la excepción correcta
        if (!repository.existsById(id)) {
            throw new FunkoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * Método helper para encontrar y validar la categoría
     */
    private Categoria findCategoriaById(Long categoriaId) {
        // Asumimos que para POST/PUT, el @Valid del controlador ya ha forzado @NotNull
        if (categoriaId == null) {
            // Esto solo saltaría en un PATCH si se pasa "categoryId": null
            // o si create/update no tienen @Valid
            throw new FunkoException("El ID de la categoría no puede ser nulo");
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new FunkoException("Categoría no encontrada con ID: " + categoriaId));
        // Idealmente, aquí se usaría una CategoriaNotFoundException
    }

    /**
     * Reglas de negocio (replicadas para PATCH)
     */
    private void validarNegocio(FunkoRequestDto dto) {
        // Nombre
        if (dto.getNombre() != null) {
            if (dto.getNombre().trim().isEmpty()) {
                throw new FunkoException("El nombre no puede estar vacío");
            }
            if (dto.getNombre().length() > 100) {
                throw new FunkoException("El nombre no puede superar 100 caracteres");
            }
        }

        // Precio
        if (dto.getPrecio() != null && dto.getPrecio() <= 0) {
            throw new FunkoException("El precio debe ser mayor que 0");
        }

        // Fecha de lanzamiento
        if (dto.getFechaLanzamiento() != null && dto.getFechaLanzamiento().isAfter(LocalDate.now())) {
            throw new FunkoException("La fecha de lanzamiento no puede ser futura");
        }
    }
}