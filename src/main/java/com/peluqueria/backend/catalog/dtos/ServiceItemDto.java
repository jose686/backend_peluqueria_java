package com.peluqueria.backend.catalog.dtos;

import com.peluqueria.backend.catalog.entities.ServiceItem;


import java.math.BigDecimal;
import java.util.UUID;

public record ServiceItemDto(
    UUID id,
    String nombre,
    BigDecimal precio,
    Integer duracionMinutos
) {
    public static ServiceItemDto fromEntity(ServiceItem entity) {
        if (entity == null) return null;
        return new ServiceItemDto(
            entity.getId(),
            entity.getNombre(),
            entity.getPrecio(),
            entity.getDuracionMinutos()
        );
    }
}
