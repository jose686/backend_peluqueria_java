package com.peluqueria.backend.catalog.controllers;

import com.peluqueria.backend.catalog.dtos.ServiceItemDto;
import com.peluqueria.backend.catalog.entities.CatalogType;
import com.peluqueria.backend.catalog.repositories.CatalogItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceItemController {

    private final CatalogItemRepository catalogItemRepository;

    @Autowired
    public ServiceItemController(CatalogItemRepository catalogItemRepository) {
        this.catalogItemRepository = catalogItemRepository;
    }

    /**
     * Endpoint para obtener el listado de todos los servicios activos del catálogo unificado.
     */
    @GetMapping
    public ResponseEntity<List<ServiceItemDto>> getAll() {
        List<ServiceItemDto> dtos = catalogItemRepository.findByTipoAndActivoTrue(CatalogType.SERVICIO).stream()
                .map(item -> new ServiceItemDto(
                        item.getId().toString(),
                        item.getNombre(),
                        item.getPrecio(),
                        item.getDuracionMinutos() != null ? item.getDuracionMinutos() : 30
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint para consultar los detalles de un servicio por su identificador.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var opt = catalogItemRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(errorMap("Servicio no encontrado"));
        }
        return ResponseEntity.ok(ServiceItemDto.fromCatalogItem(opt.get()));
    }

    private Map<String, String> errorMap(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("error", message);
        return map;
    }
}
