package srangeldev.funkoapi.Funko.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import srangeldev.funkoapi.Categoria.mappers.CategoriaMapper;
import srangeldev.funkoapi.Funko.dto.FunkoResponseDto;
import srangeldev.funkoapi.Funko.models.Funko;

/**
 * Mapper para convertir entre la entidad Funko y sus DTOs.
 */
@Component
public class FunkoMapper {

    private final CategoriaMapper categoriaMapper;

    @Autowired
    public FunkoMapper(CategoriaMapper categoriaMapper) {
        this.categoriaMapper = categoriaMapper;
    }

    /**
     * Convierte una entidad Funko a un FunkoResponseDto.
     *
     * @param funko La entidad Funko a convertir.
     * @return El DTO con los datos del Funko.
     */
    public FunkoResponseDto toResponse(Funko funko) {
        if (funko == null) {
            return null;
        }

        // Usamos el CategoriaMapper para convertir la entidad Categoria anidada
        var categoriaDto = funko.getCategoria() != null
                ? categoriaMapper.toResponseDto(funko.getCategoria())
                : null;

        return FunkoResponseDto.builder()
                .id(funko.getId())
                .uuid(funko.getUuid())
                .nombre(funko.getNombre())
                .precio(funko.getPrecio())
                .fechaLanzamiento(funko.getFechaLanzamiento())
                .categoriaResponseDto(categoriaDto)
                .createdAt(funko.getCreatedAt())
                .updatedAt(funko.getUpdatedAt())
                .build();
    }

    // Nota: No incluimos un mapper de 'RequestDto' a 'Funko' porque
    // el servicio necesita lógica adicional (buscar la Categoría por ID)
    // que no encaja limpiamente en un mapper simple.
}