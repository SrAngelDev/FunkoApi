package srangeldev.funkoapi.services;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import srangeldev.funkoapi.dto.FunkoCreateDTO;
import srangeldev.funkoapi.exceptions.FunkoNotFoundException;
import srangeldev.funkoapi.models.Funko;
import srangeldev.funkoapi.repositories.FunkoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación del servicio que delega el almacenamiento en un repositorio en memoria.
 * También incorpora caché con Spring Cache.
 */
@Service
@CacheConfig(cacheNames = "funkos")
public class FunkoServiceImpl implements FunkoService {

    private final FunkoRepository repository;

    public FunkoServiceImpl(FunkoRepository repository) {
        this.repository = repository;
    }

    @Override
    @CachePut(key = "#result.id")
    public Funko create(FunkoCreateDTO dto) {
        // Validación simple extra (además de la de anotaciones)
        validarNegocio(dto);
        Funko funko = new Funko(null, dto.getNombre(), dto.getPrecio(), dto.getCategoria(), dto.getFechaLanzamiento(), null, null);
        return repository.save(funko);
    }

    @Override
    @Cacheable(key = "#id")
    public Funko getById(Long id) {
        return repository.getById(id).orElseThrow(() -> new FunkoNotFoundException(id));
    }

    @Override
    public List<Funko> getAll() {
        // Para la lista completa, no usamos caché para simplificar invalidaciones.
        return repository.getAll();
    }

    @Override
    @CachePut(key = "#id")
    public Funko update(Long id, FunkoCreateDTO dto) {
        validarNegocio(dto);
        Funko cambios = new Funko(null, dto.getNombre(), dto.getPrecio(), dto.getCategoria(), dto.getFechaLanzamiento(), null, null);
        return repository.update(id, cambios).orElseThrow(() -> new FunkoNotFoundException(id));
    }

    @Override
    @CachePut(key = "#id")
    public Funko patch(Long id, FunkoCreateDTO dto) {
        // Validamos solo las reglas de negocio aplicables a los campos presentes
        validarNegocio(dto);
        Funko cambios = new Funko(null, dto.getNombre(), dto.getPrecio(), dto.getCategoria(), dto.getFechaLanzamiento(), null, null);
        return repository.patch(id, cambios).orElseThrow(() -> new FunkoNotFoundException(id));
    }

    @Override
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        repository.deleteById(id).orElseThrow(() -> new FunkoNotFoundException(id));
    }

    // Reglas sencillas de negocio más allá de la validación de anotaciones
    // Nota:
    // - En POST/PUT ya usamos @Valid en el controlador, por lo que las anotaciones del DTO se aplican.
    // - En PATCH no usamos @Valid para permitir campos opcionales; por eso aquí replicamos
    //   las reglas clave sólo para los campos presentes en el DTO.
    private void validarNegocio(FunkoCreateDTO dto) {
        // Nombre: no puede ser cadena vacía ni superar 100 caracteres (si se envía)
        if (dto.getNombre() != null) {
            if (dto.getNombre().trim().isEmpty()) {
                throw new srangeldev.funkoapi.exceptions.FunkoException("El nombre no puede estar vacío");
            }
            if (dto.getNombre().length() > 100) {
                throw new srangeldev.funkoapi.exceptions.FunkoException("El nombre no puede superar 100 caracteres");
            }
        }

        // Precio: debe ser > 0 (si se envía)
        if (dto.getPrecio() != null && dto.getPrecio() <= 0) {
            throw new srangeldev.funkoapi.exceptions.FunkoException("El precio debe ser mayor que 0");
        }

        // Fecha de lanzamiento: no puede ser futura (si se envía)
        if (dto.getFechaLanzamiento() != null && dto.getFechaLanzamiento().isAfter(LocalDate.now())) {
            throw new srangeldev.funkoapi.exceptions.FunkoException("La fecha de lanzamiento no puede ser futura");
        }
    }
}
