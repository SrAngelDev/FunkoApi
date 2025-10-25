package srangeldev.funkoapi.Categoria.services;

import srangeldev.funkoapi.Categoria.dto.CategoriaRequestDto;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;

import java.util.List;

public interface CategoriaService {
    List<CategoriaResponseDto> getAll();
    CategoriaResponseDto getById(Long id);
    CategoriaResponseDto create(CategoriaRequestDto dto);
    CategoriaResponseDto update(Long id, CategoriaRequestDto dto);
    void delete(Long id);
}