package com.peluqueria.backend.catalog.services;

import com.peluqueria.backend.catalog.dtos.ServiceItemDto;
import com.peluqueria.backend.catalog.dtos.ServiceItemRequest;
import com.peluqueria.backend.catalog.entities.ServiceItem;
import com.peluqueria.backend.catalog.repositories.ServiceItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ServiceItemServiceImpl implements ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    @Autowired
    public ServiceItemServiceImpl(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    /**
     * Recupera y mapea a DTO todos los servicios disponibles.
     */
    @Transactional(readOnly = true)
    public List<ServiceItemDto> getAll() {
        return serviceItemRepository.findAll().stream()
                .map(ServiceItemDto::fromEntity)
                .toList();
    }

    /**
     * Busca un servicio por su UUID único.
     */
    @Transactional(readOnly = true)
    public ServiceItemDto getById(UUID id) {
        ServiceItem entity = serviceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
        return ServiceItemDto.fromEntity(entity);
    }

    /**
     * Inserta un nuevo servicio en la base de datos.
     */
    @Transactional
    public ServiceItemDto create(ServiceItemRequest request) {
        ServiceItem entity = ServiceItem.builder()
                .nombre(request.nombre())
                .precio(request.precio())
                .duracionMinutos(request.duracionMinutos())
                .build();
        return ServiceItemDto.fromEntity(serviceItemRepository.save(entity));
    }

    /**
     * Modifica los atributos (nombre, precio, duración) de un servicio.
     */
    @Transactional
    public ServiceItemDto update(UUID id, ServiceItemRequest request) {
        ServiceItem entity = serviceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
        entity.setNombre(request.nombre());
        entity.setPrecio(request.precio());
        entity.setDuracionMinutos(request.duracionMinutos());
        return ServiceItemDto.fromEntity(serviceItemRepository.save(entity));
    }

    /**
     * Elimina el registro de un servicio por su ID.
     */
    @Transactional
    public void delete(UUID id) {
        ServiceItem entity = serviceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
        serviceItemRepository.delete(entity);
    }
}
