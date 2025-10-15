package srangeldev.funkoapi.mappers;

import org.springframework.stereotype.Component;
import srangeldev.funkoapi.dto.FunkoResponseDTO;
import srangeldev.funkoapi.models.Funko;

/**
 * Mapper sencillo para convertir entre entidades del dominio y DTOs.
 *
 * Para este proyecto básico solo necesitamos convertir Funko -> FunkoResponseDTO.
 * Mantener esta lógica en un componente dedicado ayuda a separar responsabilidades
 * y facilita su reutilización y testeo.
 */
@Component
public class FunkoMapper {

    public FunkoResponseDTO toResponse(Funko f) {
        if (f == null) return null;
        return new FunkoResponseDTO(
                f.getId(),
                f.getNombre(),
                f.getPrecio(),
                f.getCategoria(),
                f.getFechaLanzamiento(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }
}
