package com.peluqueria.backend.catalog.services;

import com.peluqueria.backend.catalog.dtos.CatalogItemRequest;
import com.peluqueria.backend.catalog.entities.CatalogItem;
import com.peluqueria.backend.catalog.entities.CatalogType;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CatalogItemRepository;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import com.peluqueria.backend.media.entities.MediaFile;
import com.peluqueria.backend.media.repositories.MediaFileRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface CatalogItemService {
    /**
     * Registra un nuevo artículo en el catálogo de productos/servicios.
     */
    CatalogItem createCatalogItem(CatalogItemRequest request);

    /**
     * Actualiza los datos de un artículo de catálogo existente.
     */
    CatalogItem updateCatalogItem(Long id, CatalogItemRequest request);

    /**
     * Recupera todos los artículos de catálogo (activos e inactivos).
     */
    List<CatalogItem> getAllCatalogItems();

    /**
     * Recupera únicamente los artículos de catálogo que están activos.
     */
    List<CatalogItem> getActiveCatalogItems();

    /**
     * Filtra los artículos de catálogo activos por su tipo (PRODUCTO o SERVICIO).
     */
    List<CatalogItem> getCatalogItemsByTipo(CatalogType tipo);

    /**
     * Busca un artículo de catálogo por su identificador único.
     */
    CatalogItem getCatalogItemById(Long id);

    /**
     * Busca un artículo de catálogo utilizando su slug único.
     */
    CatalogItem getCatalogItemBySlug(String slug);

    /**
     * Elimina permanentemente un artículo de catálogo a partir de su ID.
     */
    void deleteCatalogItem(Long id);
}
