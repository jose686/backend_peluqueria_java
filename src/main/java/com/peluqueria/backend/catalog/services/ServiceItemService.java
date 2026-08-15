package com.peluqueria.backend.catalog.services;

import com.peluqueria.backend.catalog.dtos.ServiceItemDto;
import com.peluqueria.backend.catalog.dtos.ServiceItemRequest;
import com.peluqueria.backend.catalog.entities.ServiceItem;
import com.peluqueria.backend.catalog.repositories.ServiceItemRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceItemService {
    /**
     * Obtiene una lista de todos los servicios ofrecidos en la peluquería.
     */
    List<ServiceItemDto> getAll();

    /**
     * Obtiene la información detallada de un servicio por su ID.
     */
    ServiceItemDto getById(UUID id);

    /**
     * Crea y registra un nuevo servicio en el catálogo.
     */
    ServiceItemDto create(ServiceItemRequest request);

    /**
     * Actualiza los datos de un servicio existente (nombre, precio, duración).
     */
    ServiceItemDto update(UUID id, ServiceItemRequest request);

    /**
     * Elimina permanentemente un servicio del catálogo por su ID.
     */
    void delete(UUID id);
}
