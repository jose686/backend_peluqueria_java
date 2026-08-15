package com.peluqueria.backend.blog.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BlogPostRequest(
    @NotBlank(message = "El título es obligatorio")
    String titulo,

    String slug,

    @NotBlank(message = "El contenido es obligatorio")
    String contenidoHtml,

    String resumen,

    Long portadaId,

    @NotNull(message = "La categoría es obligatoria")
    Long categoriaId,

    @NotBlank(message = "El estado es obligatorio")
    String estado
) {}
