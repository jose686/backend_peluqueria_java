package com.peluqueria.backend.catalog.dtos;

import com.peluqueria.backend.catalog.entities.Category;


public record CategoryDto(
    Long id,
    String nombre,
    String slug,
    String tipo
) {
    public static CategoryDto fromEntity(Category category) {
        if (category == null) return null;
        return new CategoryDto(
            category.getId(),
            category.getNombre(),
            category.getSlug(),
            category.getTipo().name()
        );
    }
}
