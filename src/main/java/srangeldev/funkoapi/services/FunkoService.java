package srangeldev.funkoapi.services;

import srangeldev.funkoapi.dto.FunkoRequestDto;
import srangeldev.funkoapi.models.Funko;

import java.util.List;

/**
 * Interfaz del servicio que define las operaciones de negocio para Funkos.
 */
public interface FunkoService {
    List<Funko> getAll();
    Funko getById(Long id);
    Funko create(FunkoRequestDto dto);
    Funko update(Long id, FunkoRequestDto dto);
    Funko patch(Long id, FunkoRequestDto dto);
    void delete(Long id);
}
