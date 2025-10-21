package srangeldev.funkoapi.services;

import jakarta.transaction.Transactional;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import srangeldev.funkoapi.dto.FunkoRequestDto;
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

    @Autowired
    public FunkoServiceImpl(FunkoRepository repository) {
        this.repository = repository;
    }

    @Override
    @CachePut(key = "#result.id")
    public Funko create(FunkoRequestDto dto) {
        // Validación simple extra (además de la de anotaciones)
        validarNegocio(dto);
        Funko funko = new Funko(null, dto.getNombre(), dto.getPrecio(), dto.getCategoria(), dto.getFechaLanzamiento(), null, null);
        return repository.save(funko);
    }

    @Override
    @Cacheable(key = "#id")
    public Funko getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new FunkoNotFoundException(id));
    }

    @Override
    public List<Funko> getAll() {
        // Para la lista completa, no usamos caché para simplificar invalidaciones.
        return repository.findAll();
    }

    @Override
    @CachePut(key = "#id")
    @Transactional // Esto gestiona el ciclo de vida de la entidad para poder hacer update
    public Funko update(Long id, FunkoRequestDto dto) {
        validarNegocio(dto);

        // Primero obtenemos el funko a actulizar
        Funko funkoExistente = repository.findById(id).orElseThrow(() -> new FunkoNotFoundException(id));

        // Le pasamos los nuevos campos
        funkoExistente.setNombre(dto.getNombre());
        funkoExistente.setPrecio(dto.getPrecio());
        funkoExistente.setCategoria(dto.getCategoria());
        funkoExistente.setFechaLanzamiento(dto.getFechaLanzamiento());

        // Al ser una transaccion JPA detecta que ya existe y lo actuliza en vez de crearlo
        return  repository.save(funkoExistente);
    }

    @Override
    @CachePut(key = "#id")
    @Transactional // Igual que crear para mantener el ciclo de vida de la entidad
    public Funko patch(Long id, FunkoRequestDto dto) {
        // Validamos solo las reglas de negocio aplicables a los campos presentes
        validarNegocio(dto);

        //Primero buscamos el funko a actualizar
        Funko funkoExistente = repository.findById(id).orElseThrow(() -> new FunkoNotFoundException(id));

        //Comprobamos el campo uno a uno y aplicamos solo los que no son nulos, es decir los que cambian
        if (dto.getNombre() != null) {
            funkoExistente.setNombre(dto.getNombre());
        }
        if (dto.getPrecio() != null) {
            funkoExistente.setPrecio(dto.getPrecio());
        }
        if (dto.getCategoria() != null) {
            funkoExistente.setCategoria(dto.getCategoria());
        }
        if (dto.getFechaLanzamiento() != null) {
            funkoExistente.setFechaLanzamiento(dto.getFechaLanzamiento());
        }

        //Devolvemos el funko actulizado
        return  repository.save(funkoExistente);
    }

    @Override
    @CacheEvict(key = "#id")
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Reglas sencillas de negocio más allá de la validación de anotaciones
    // Nota:
    // - En POST/PUT ya usamos @Valid en el controlador, por lo que las anotaciones del DTO se aplican.
    // - En PATCH no usamos @Valid para permitir campos opcionales; por eso aquí replicamos
    //   las reglas clave sólo para los campos presentes en el DTO.
    private void validarNegocio(FunkoRequestDto dto) {
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
