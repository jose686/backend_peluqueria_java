package com.peluqueria.backend.blog.controllers;

import com.peluqueria.backend.blog.dtos.BlogCategoryRequest;
import com.peluqueria.backend.blog.services.BlogCategoryService;
import com.peluqueria.backend.catalog.dtos.CategoryDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestController
@RequestMapping({"/api/blog/categories", "/api/v1/blog/categories"})
public class BlogCategoryController {
    private final BlogCategoryService blogCategoryService;

    public BlogCategoryController(BlogCategoryService blogCategoryService) {
        this.blogCategoryService = blogCategoryService;
    }

    @GetMapping
    public List<CategoryDto> list() {
        return blogCategoryService.getAll().stream().map(CategoryDto::fromEntity).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody BlogCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryDto.fromEntity(blogCategoryService.create(request.nombre())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto update(@PathVariable Long id, @Valid @RequestBody BlogCategoryRequest request) {
        return CategoryDto.fromEntity(blogCategoryService.update(id, request.nombre()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blogCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCategory(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
}
