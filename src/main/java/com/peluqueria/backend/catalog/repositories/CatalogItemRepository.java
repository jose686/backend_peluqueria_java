package com.peluqueria.backend.catalog.repositories;

import com.peluqueria.backend.catalog.entities.CatalogItem;
import com.peluqueria.backend.catalog.entities.CatalogType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
    Optional<CatalogItem> findBySlug(String slug);
    List<CatalogItem> findByActivoTrue();
    List<CatalogItem> findByTipoAndActivoTrue(CatalogType tipo);
    List<CatalogItem> findByCategoriaId(Long categoriaId);
}
