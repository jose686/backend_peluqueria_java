package com.peluqueria.backend.staff.controllers;

import com.peluqueria.backend.staff.dtos.WorkerDto;
import com.peluqueria.backend.staff.dtos.WorkerRequest;
import com.peluqueria.backend.staff.services.WorkerService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final WorkerService workerService;

    @Autowired
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    /**
     * Endpoint para listar todos los trabajadores del establecimiento.
     */
    @GetMapping
    public ResponseEntity<List<WorkerDto>> getAll() {
        return ResponseEntity.ok(workerService.getAll());
    }

    /**
     * Endpoint para obtener el perfil de un trabajador por su identificador.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(workerService.getById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(errorMap(ex.getMessage()));
        }
    }

    /**
     * Endpoint para registrar un nuevo trabajador (sólo accesible para administradores).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody WorkerRequest request) {
        try {
            WorkerDto created = workerService.create(request);
            return ResponseEntity
                    .created(URI.create("/api/v1/workers/" + created.id()))
                    .body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(errorMap(ex.getMessage()));
        }
    }

    /**
     * Endpoint para actualizar los datos de un trabajador (sólo accesible para administradores).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody WorkerRequest request) {
        try {
            return ResponseEntity.ok(workerService.update(id, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(errorMap(ex.getMessage()));
        }
    }

    /**
     * Endpoint para dar de baja/eliminar a un trabajador (sólo accesible para administradores).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            workerService.delete(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Trabajador eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(errorMap(ex.getMessage()));
        }
    }

    private Map<String, String> errorMap(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("error", message);
        return map;
    }
}
