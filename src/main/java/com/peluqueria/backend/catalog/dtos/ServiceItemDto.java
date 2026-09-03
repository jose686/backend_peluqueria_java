package com.peluqueria.backend.catalog.dtos;

import com.peluqueria.backend.catalog.entities.CatalogItem;

import java.math.BigDecimal;

public record ServiceItemDto(
    String id,
    String nombre,
    BigDecimal precio,
    Integer duracionMinutos
) {
    public static ServiceItemDto fromCatalogItem(CatalogItem item) {
        if (item == null) return null;
        return new ServiceItemDto(
            item.getId() != null ? item.getId().toString() : null,
            item.getNombre(),
            item.getPrecio(),
            item.getDuracionMinutos() != null ? item.getDuracionMinutos() : 30
        );
    }
}
