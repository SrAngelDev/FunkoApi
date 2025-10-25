package srangeldev.funkoapi.Categoria.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import srangeldev.funkoapi.Categoria.dto.CategoriaRequestDto;
import srangeldev.funkoapi.Categoria.dto.CategoriaResponseDto;
import srangeldev.funkoapi.Categoria.services.CategoriaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias") // Ruta base para el controlador
public class CategoriaController {

    private final CategoriaService service;

    @Autowired
    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<List<CategoriaResponseDto>> getAllCategorias() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> getCategoriaById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("")
    public ResponseEntity<CategoriaResponseDto> createCategoria(
            @Valid @RequestBody CategoriaRequestDto dto
    ) {
        CategoriaResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> updateCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDto dto
    ) {
        CategoriaResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    // Manejador para las excepciones de validación (@Valid)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}