package com.peluqueria.backend.catalog.dtos;

import com.peluqueria.backend.catalog.entities.CatalogItem;
import com.peluqueria.backend.media.dtos.MediaFileDto;


import java.math.BigDecimal;

public record CatalogItemDto(
    Long id,
    String nombre,
    String slug,
    String descripcion,
    BigDecimal precio,
    String tipo,
    Integer duracionMinutos,
    Integer stock,
    MediaFileDto portada,
    CategoryDto categoria,
    Boolean activo
) {
    public static CatalogItemDto fromEntity(CatalogItem item) {
        if (item == null) return null;
        return new CatalogItemDto(
            item.getId(),
            item.getNombre(),
            item.getSlug(),
            item.getDescripcion(),
            item.getPrecio(),
            item.getTipo().name(),
            item.getDuracionMinutos(),
            item.getStock(),
            MediaFileDto.fromEntity(item.getPortada()),
            CategoryDto.fromEntity(item.getCategoria()),
            item.getActivo()
        );
    }
}
