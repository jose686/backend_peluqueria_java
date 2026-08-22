package com.peluqueria.backend.catalog.controllers;

import com.peluqueria.backend.catalog.dtos.CategoryDto;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.services.CategoryService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/categories", "/api/v1/categories"})
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Endpoint para crear una categoría (sólo accesible para administradores).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto request) {
        try {
            Category category = categoryService.createCategory(request);
            return ResponseEntity.ok(CategoryDto.fromEntity(category));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para actualizar una categoría (sólo accesible para administradores).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto request) {
        try {
            Category category = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(CategoryDto.fromEntity(category));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para listar todas las categorías.
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(
            @RequestParam(value = "type", required = false) String type) {
        List<Category> source = type == null || type.isBlank()
                ? categoryService.getAllCategories()
                : categoryService.getCategoriesByTipo(CategoryType.valueOf(type.toUpperCase()));
        List<CategoryDto> categories = source.stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    /**
     * Endpoint para consultar una categoría por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(CategoryDto.fromEntity(category));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Endpoint para consultar una categoría por su slug.
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getCategoryBySlug(@PathVariable String slug) {
        try {
            Category category = categoryService.getCategoryBySlug(slug);
            return ResponseEntity.ok(CategoryDto.fromEntity(category));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Endpoint para filtrar categorías por su tipo de uso.
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> getCategoriesByTipo(@PathVariable String tipo) {
        try {
            CategoryType categoryType = CategoryType.valueOf(tipo.toUpperCase());
            List<CategoryDto> categories = categoryService.getCategoriesByTipo(categoryType).stream()
                    .map(CategoryDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(categories);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Tipo de categoría no válido: " + tipo);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para eliminar permanentemente una categoría (sólo accesible para administradores).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Categoría eliminada con éxito");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}
