package com.peluqueria.backend.catalog.repositories;

import com.peluqueria.backend.catalog.entities.ServiceItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, UUID> {
}
