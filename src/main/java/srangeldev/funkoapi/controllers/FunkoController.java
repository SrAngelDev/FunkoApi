package srangeldev.funkoapi.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import srangeldev.funkoapi.dto.FunkoCreateDTO;
import srangeldev.funkoapi.dto.FunkoResponseDTO;
import srangeldev.funkoapi.mappers.FunkoMapper;
import srangeldev.funkoapi.models.Funko;
import srangeldev.funkoapi.services.FunkoService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de Funkos.
 *
 * Sólo orquesta las peticiones/respuestas y delega la lógica en el servicio.
 */
@RestController
@RequestMapping("/funkos")
@Validated
public class FunkoController {

    private final FunkoService funkoService;
    private final FunkoMapper mapper;

    public FunkoController(FunkoService funkoService, FunkoMapper mapper) {
        this.funkoService = funkoService;
        this.mapper = mapper;
    }

    // Obtener todos los Funkos
    @GetMapping
    @RequestMapping({"/", ""})
    public ResponseEntity<List<FunkoResponseDTO>> getAll() {
        List<FunkoResponseDTO> lista = funkoService.getAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Obtener un Funko por ID
    @GetMapping("/{id}")
    public ResponseEntity<FunkoResponseDTO> getById(@PathVariable Long id) {
        Funko funko = funkoService.getById(id);
        return ResponseEntity.ok(mapper.toResponse(funko));
    }

    // Crear un nuevo Funko
    @PostMapping
    public ResponseEntity<FunkoResponseDTO> create(@Valid @RequestBody FunkoCreateDTO dto) {
        Funko creado = funkoService.create(dto);
        FunkoResponseDTO resp = mapper.toResponse(creado);
        // Devolvemos 201 Created con Location del recurso
        return ResponseEntity.created(URI.create("/api/funkos/" + resp.getId())).body(resp);
    }

    // Actualizar un Funko por ID (PUT completo)
    @PutMapping("/{id}")
    public ResponseEntity<FunkoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody FunkoCreateDTO dto) {
        Funko actualizado = funkoService.update(id, dto);
        return ResponseEntity.ok(mapper.toResponse(actualizado));
    }

    // Actualización parcial (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<FunkoResponseDTO> patch(@PathVariable Long id, @RequestBody FunkoCreateDTO dto) {
        // Sin @Valid para permitir campos opcionales en PATCH
        Funko actualizado = funkoService.patch(id, dto);
        return ResponseEntity.ok(mapper.toResponse(actualizado));
    }

    // Eliminar un Funko por ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        funkoService.delete(id);
    }
}
