package com.peluqueria.backend.catalog.controllers;

import com.peluqueria.backend.catalog.dtos.CatalogItemDto;
import com.peluqueria.backend.catalog.dtos.CatalogItemRequest;
import com.peluqueria.backend.catalog.entities.CatalogItem;
import com.peluqueria.backend.catalog.entities.CatalogType;
import com.peluqueria.backend.catalog.services.CatalogItemService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogItemController {

    private final CatalogItemService catalogItemService;

    @Autowired
    public CatalogItemController(CatalogItemService catalogItemService) {
        this.catalogItemService = catalogItemService;
    }

    /**
     * Endpoint para registrar un artículo en el catálogo (sólo accesible para administradores).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCatalogItem(@Valid @RequestBody CatalogItemRequest request) {
        try {
            CatalogItem item = catalogItemService.createCatalogItem(request);
            return ResponseEntity.ok(CatalogItemDto.fromEntity(item));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para actualizar los datos de un artículo del catálogo (sólo accesible para administradores).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCatalogItem(@PathVariable Long id, @Valid @RequestBody CatalogItemRequest request) {
        try {
            CatalogItem item = catalogItemService.updateCatalogItem(id, request);
            return ResponseEntity.ok(CatalogItemDto.fromEntity(item));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para listar los artículos del catálogo (opcionalmente todos si es administrador, o sólo activos).
     */
    @GetMapping
    public ResponseEntity<List<CatalogItemDto>> getAllCatalogItems(
            @RequestParam(value = "all", defaultValue = "false") boolean all,
            Authentication authentication) {
        
        List<CatalogItem> items;
        // If "all" is true and user is admin or empleado, return all items (including inactive ones)
        if (all && authentication != null && (
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))) {
            items = catalogItemService.getAllCatalogItems();
        } else {
            items = catalogItemService.getActiveCatalogItems();
        }

        List<CatalogItemDto> dtos = items.stream()
                .map(CatalogItemDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint para consultar detalles de un artículo por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCatalogItemById(@PathVariable Long id) {
        try {
            CatalogItem item = catalogItemService.getCatalogItemById(id);
            return ResponseEntity.ok(CatalogItemDto.fromEntity(item));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Endpoint para consultar detalles de un artículo por su slug de URL.
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getCatalogItemBySlug(@PathVariable String slug) {
        try {
            CatalogItem item = catalogItemService.getCatalogItemBySlug(slug);
            return ResponseEntity.ok(CatalogItemDto.fromEntity(item));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Endpoint para consultar los artículos activos filtrados por tipo (PRODUCTO o SERVICIO).
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> getCatalogItemsByTipo(@PathVariable String tipo) {
        try {
            CatalogType catalogType = CatalogType.valueOf(tipo.toUpperCase());
            List<CatalogItemDto> dtos = catalogItemService.getCatalogItemsByTipo(catalogType).stream()
                    .map(CatalogItemDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Tipo de catálogo no válido: " + tipo);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para eliminar definitivamente un artículo del catálogo (sólo accesible para administradores).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCatalogItem(@PathVariable Long id) {
        try {
            catalogItemService.deleteCatalogItem(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Artículo del catálogo eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}
