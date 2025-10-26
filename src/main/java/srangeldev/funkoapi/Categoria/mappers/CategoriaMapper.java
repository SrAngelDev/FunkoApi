package srangeldev.funkoapi.Categoria.mappers;

import org.springframework.stereotype.Component;
import srangeldev.funkoapi.Categoria.dto.CategoriaRequestDto;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;
import srangeldev.funkoapi.Categoria.models.Categoria;

@Component
public class CategoriaMapper {

    /**
     * Mapea un DTO de petición a una entidad Categoria.
     * La auditoría (@CreatedDate/@LastModifiedDate) gestionará las fechas.
     */
    public Categoria toCategoria(CategoriaRequestDto dto) {
        return new Categoria(
                null,
                dto.getNombre(),
                null, // createdAt es gestionado por JPA
                null  // updatedAt es gestionado por JPA
        );
    }

    /**
     * Mapea una entidad Categoria a un DTO de respuesta.
     */
    public CategoriaResponseDto toResponseDto(Categoria categoria) {
        return new CategoriaResponseDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getCreatedAt(),
                categoria.getUpdatedAt()
        );
    }
}