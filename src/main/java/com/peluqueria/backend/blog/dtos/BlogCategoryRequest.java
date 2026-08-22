package com.peluqueria.backend.blog.dtos;

import jakarta.validation.constraints.NotBlank;

public record BlogCategoryRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio") String nombre) {
}
