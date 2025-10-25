package srangeldev.funkoapi.Categoria.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import srangeldev.funkoapi.Categoria.dto.CategoriaRequestDto;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaException;
import srangeldev.funkoapi.Categoria.exceptions.CategoriaNotFoundException;
import srangeldev.funkoapi.Categoria.mappers.CategoriaMapper;
import srangeldev.funkoapi.Categoria.models.Categoria;
import srangeldev.funkoapi.Categoria.repositories.CategoriaRepository;
import srangeldev.funkoapi.Funko.repositories.FunkoRepository; // Necesario para la validación de borrado

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final FunkoRepository funkoRepository; // Para comprobar dependencias
    private final CategoriaMapper mapper;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, FunkoRepository funkoRepository, CategoriaMapper mapper) {
        this.categoriaRepository = categoriaRepository;
        this.funkoRepository = funkoRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CategoriaResponseDto> getAll() {
        return categoriaRepository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaResponseDto getById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
        return mapper.toResponseDto(categoria);
    }

    @Override
    public CategoriaResponseDto create(CategoriaRequestDto dto) {
        // Comprobar si el nombre ya existe
        categoriaRepository.findByNombre(dto.getNombre()).ifPresent(c -> {
            throw new CategoriaException("El nombre '" + dto.getNombre() + "' ya existe.");
        });

        Categoria categoria = mapper.toCategoria(dto);
        Categoria savedCategoria = categoriaRepository.save(categoria);
        return mapper.toResponseDto(savedCategoria);
    }

    @Override
    public CategoriaResponseDto update(Long id, CategoriaRequestDto dto) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));

        // Comprobar si el nuevo nombre ya existe EN OTRA categoría
        categoriaRepository.findByNombre(dto.getNombre()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new CategoriaException("El nombre '" + dto.getNombre() + "' ya existe.");
            }
        });

        categoriaExistente.setNombre(dto.getNombre());
        // 'updatedAt' se actualiza automáticamente por JPA Auditing
        Categoria updatedCategoria = categoriaRepository.save(categoriaExistente);
        return mapper.toResponseDto(updatedCategoria);
    }

    @Override
    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNotFoundException(id);
        }

        // Comprobar si hay Funkos asociados a esta categoría
        if (funkoRepository.existsByCategoria_Id(id)) {
            throw new CategoriaException("No se puede borrar la categoría. Tiene Funkos asociados.");
        }

        categoriaRepository.deleteById(id);
    }
}