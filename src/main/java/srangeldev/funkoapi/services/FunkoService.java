package srangeldev.funkoapi.services;

import srangeldev.funkoapi.dto.FunkoCreateDTO;
import srangeldev.funkoapi.models.Funko;

import java.util.List;

/**
 * Interfaz del servicio que define las operaciones de negocio para Funkos.
 */
public interface FunkoService {
    List<Funko> getAll();
    Funko getById(Long id);
    Funko create(FunkoCreateDTO dto);
    Funko update(Long id, FunkoCreateDTO dto);
    Funko patch(Long id, FunkoCreateDTO dto);
    void delete(Long id);
}
