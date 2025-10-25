package srangeldev.funkoapi.Funko.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import srangeldev.funkoapi.Funko.dto.FunkoRequestDto;
import srangeldev.funkoapi.Funko.dto.FunkoResponseDto;
import srangeldev.funkoapi.Funko.mappers.FunkoMapper;
import srangeldev.funkoapi.Funko.models.Funko;
import srangeldev.funkoapi.Funko.services.FunkoService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de Funkos.
 *
 * Sólo orquesta las peticiones/respuestas y delega la lógica en el servicio.
 */
@RestController
@RequestMapping("/api/funkos") // <-- ÚNICO CAMBIO SUGERIDO (para consistencia)
@Validated
public class FunkoController {

    private final FunkoService funkoService;
    private final FunkoMapper mapper;

    public FunkoController(FunkoService funkoService, FunkoMapper mapper) {
        this.funkoService = funkoService;
        this.mapper = mapper;
    }

    // Obtener todos los Funkos
    @GetMapping({"/", ""}) // Esta ruta ahora será /api/funkos/
    public ResponseEntity<List<FunkoResponseDto>> getAll() {
        // El servicio devuelve List<Funko>, el mapper convierte a List<FunkoResponseDto>
        List<FunkoResponseDto> lista = funkoService.getAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // Obtener un Funko por ID
    @GetMapping("/{id}") // Esta ruta ahora será /api/funkos/1
    public ResponseEntity<FunkoResponseDto> getById(@PathVariable Long id) {
        // El servicio devuelve Funko, el mapper convierte a FunkoResponseDto
        Funko funko = funkoService.getById(id);
        return ResponseEntity.ok(mapper.toResponse(funko));
    }

    // Crear un nuevo Funko
    @PostMapping({"/", ""})
    public ResponseEntity<FunkoResponseDto> create(@Valid @RequestBody FunkoRequestDto dto) {
        // El servicio lo procesa y devuelve la entidad Funko
        Funko creado = funkoService.create(dto);
        // El mapper convierte la entidad al DTO de respuesta
        FunkoResponseDto resp = mapper.toResponse(creado);
        return ResponseEntity.created(URI.create("/api/funkos/" + resp.getId())).body(resp);
    }

    // Actualizar un Funko por ID (PUT completo)
    @PutMapping("/{id}")
    public ResponseEntity<FunkoResponseDto> update(@PathVariable Long id, @Valid @RequestBody FunkoRequestDto dto) {
        Funko actualizado = funkoService.update(id, dto);
        return ResponseEntity.ok(mapper.toResponse(actualizado));
    }

    // Actualización parcial (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<FunkoResponseDto> patch(@PathVariable Long id, @RequestBody FunkoRequestDto dto) {
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