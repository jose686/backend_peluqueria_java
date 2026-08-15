package com.peluqueria.backend.catalog.services;

import com.peluqueria.backend.catalog.dtos.CategoryDto;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea una categoría asignando un slug a partir de su nombre.
     */
    @Transactional
    public Category createCategory(CategoryDto request) {
        String slug = Category.slugify(request.nombre());
        if (categoryRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre o slug");
        }

        Category category = Category.builder()
                .nombre(request.nombre())
                .tipo(CategoryType.valueOf(request.tipo()))
                .build();

        return categoryRepository.save(category);
    }

    /**
     * Actualiza el nombre y tipo de una categoría.
     */
    @Transactional
    public Category updateCategory(Long id, CategoryDto request) {
        Category category = getCategoryById(id);
        
        String newSlug = Category.slugify(request.nombre());
        if (!category.getSlug().equals(newSlug) && categoryRepository.existsBySlug(newSlug)) {
            throw new IllegalArgumentException("Ya existe otra categoría con ese nombre o slug");
        }

        category.setNombre(request.nombre());
        category.setTipo(CategoryType.valueOf(request.tipo()));

        return categoryRepository.save(category);
    }

    /**
     * Recupera una categoría por su identificador.
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id: " + id));
    }

    /**
     * Recupera una categoría utilizando su slug.
     */
    @Transactional(readOnly = true)
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con slug: " + slug));
    }

    /**
     * Filtra categorías por su tipo.
     */
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByTipo(CategoryType tipo) {
        return categoryRepository.findByTipo(tipo);
    }

    /**
     * Obtiene el listado completo de categorías.
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Elimina el registro de una categoría.
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
