package com.peluqueria.backend.catalog.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CatalogItemRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    String slug,

    String descripcion,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    BigDecimal precio,

    @NotBlank(message = "El tipo (SERVICIO o PRODUCTO) es obligatorio")
    String tipo,

    Integer duracionMinutos,

    Integer stock,

    Long portadaId,

    @NotNull(message = "La categoría es obligatoria")
    Long categoriaId,

    Boolean activo
) {}
