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



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogItemServiceImpl implements CatalogItemService {

    private final CatalogItemRepository catalogItemRepository;
    private final CategoryRepository categoryRepository;
    private final MediaFileRepository mediaFileRepository;

    @Autowired
    public CatalogItemServiceImpl(CatalogItemRepository catalogItemRepository,
                              CategoryRepository categoryRepository,
                              MediaFileRepository mediaFileRepository) {
        this.catalogItemRepository = catalogItemRepository;
        this.categoryRepository = categoryRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    /**
     * Crea un artículo en el catálogo, validando la categoría y guardando la portada si existe.
     */
    @Transactional
    public CatalogItem createCatalogItem(CatalogItemRequest request) {
        String slug = request.slug();
        if (slug == null || slug.isBlank()) {
            slug = Category.slugify(request.nombre());
        } else {
            slug = Category.slugify(slug);
        }

        if (catalogItemRepository.findBySlug(slug).isPresent()) {
            throw new IllegalArgumentException("Ya existe un artículo del catálogo con ese nombre o slug");
        }

        Category category = null;
        if (request.categoriaId() != null) {
            category = categoryRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            if (category.getTipo() != CategoryType.CATALOGO) {
                throw new IllegalArgumentException("La categoría seleccionada debe ser de tipo CATALOGO");
            }
        }

        MediaFile portada = null;
        if (request.portadaId() != null) {
            portada = mediaFileRepository.findById(request.portadaId())
                    .orElseThrow(() -> new IllegalArgumentException("Archivo multimedia de portada no encontrado"));
        }

        CatalogType tipo = CatalogType.valueOf(request.tipo().toUpperCase());

        CatalogItem item = CatalogItem.builder()
                .nombre(request.nombre())
                .slug(slug)
                .descripcion(request.descripcion())
                .precio(request.precio())
                .tipo(tipo)
                .duracionMinutos(request.duracionMinutos())
                .stock(request.stock())
                .portada(portada)
                .categoria(category)
                .activo(request.activo() == null || request.activo())
                .build();

        return catalogItemRepository.save(item);
    }

    /**
     * Actualiza la información de un artículo en el catálogo.
     */
    @Transactional
    public CatalogItem updateCatalogItem(Long id, CatalogItemRequest request) {
        CatalogItem item = getCatalogItemById(id);

        String slug = request.slug();
        if (slug == null || slug.isBlank()) {
            slug = Category.slugify(request.nombre());
        } else {
            slug = Category.slugify(slug);
        }

        if (!item.getSlug().equals(slug) && catalogItemRepository.findBySlug(slug).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro artículo del catálogo con ese nombre o slug");
        }

        Category category = null;
        if (request.categoriaId() != null) {
            category = categoryRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            if (category.getTipo() != CategoryType.CATALOGO) {
                throw new IllegalArgumentException("La categoría seleccionada debe ser de tipo CATALOGO");
            }
        }

        MediaFile portada = null;
        if (request.portadaId() != null) {
            portada = mediaFileRepository.findById(request.portadaId())
                    .orElseThrow(() -> new IllegalArgumentException("Archivo multimedia de portada no encontrado"));
        }

        CatalogType tipo = CatalogType.valueOf(request.tipo().toUpperCase());

        item.setNombre(request.nombre());
        item.setSlug(slug);
        item.setDescripcion(request.descripcion());
        item.setPrecio(request.precio());
        item.setTipo(tipo);
        item.setDuracionMinutos(request.duracionMinutos());
        item.setStock(request.stock());
        item.setPortada(portada);
        item.setCategoria(category);
        if (request.activo() != null) {
            item.setActivo(request.activo());
        }

        return catalogItemRepository.save(item);
    }

    /**
     * Recupera la totalidad de artículos en el catálogo.
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getAllCatalogItems() {
        return catalogItemRepository.findAll();
    }

    /**
     * Recupera sólo los artículos activos.
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getActiveCatalogItems() {
        return catalogItemRepository.findByActivoTrue();
    }

    /**
     * Filtra los artículos activos por tipo (PRODUCTO o SERVICIO).
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getCatalogItemsByTipo(CatalogType tipo) {
        return catalogItemRepository.findByTipoAndActivoTrue(tipo);
    }

    /**
     * Busca un artículo por su ID.
     */
    @Transactional(readOnly = true)
    public CatalogItem getCatalogItemById(Long id) {
        return catalogItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artículo del catálogo no encontrado con id: " + id));
    }

    /**
     * Busca un artículo por su slug de URL.
     */
    @Transactional(readOnly = true)
    public CatalogItem getCatalogItemBySlug(String slug) {
        return catalogItemRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Artículo del catálogo no encontrado con slug: " + slug));
    }

    /**
     * Elimina físicamente el registro de un artículo.
     */
    @Transactional
    public void deleteCatalogItem(Long id) {
        CatalogItem item = getCatalogItemById(id);
        catalogItemRepository.delete(item);
    }
}
