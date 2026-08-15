package com.peluqueria.backend.catalog.controllers;

import com.peluqueria.backend.catalog.dtos.ServiceItemDto;
import com.peluqueria.backend.catalog.dtos.ServiceItemRequest;
import com.peluqueria.backend.catalog.services.ServiceItemService;

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
@RequestMapping("/api/v1/services")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @Autowired
    public ServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    /**
     * Endpoint para obtener el listado de todos los servicios.
     */
    @GetMapping
    public ResponseEntity<List<ServiceItemDto>> getAll() {
        return ResponseEntity.ok(serviceItemService.getAll());
    }

    /**
     * Endpoint para consultar los detalles de un servicio por su identificador.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(serviceItemService.getById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(errorMap(ex.getMessage()));
        }
    }

    /**
     * Endpoint para registrar un nuevo servicio (sólo accesible para administradores).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody ServiceItemRequest request) {
        try {
            ServiceItemDto created = serviceItemService.create(request);
            return ResponseEntity
                    .created(URI.create("/api/v1/services/" + created.id()))
                    .body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(errorMap(ex.getMessage()));
        }
    }

    /**
     * Endpoint para actualizar los datos de un servicio (sólo accesible para administradores).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody ServiceItemRequest request) {
        try {
            return ResponseEntity.ok(serviceItemService.update(id, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(errorMap(ex.getMessage()));
        }
    }

    /**
     * Endpoint para eliminar un servicio del catálogo (sólo accesible para administradores).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            serviceItemService.delete(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Servicio eliminado con éxito");
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
