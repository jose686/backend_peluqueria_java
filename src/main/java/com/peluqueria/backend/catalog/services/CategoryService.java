package com.peluqueria.backend.catalog.services;

import com.peluqueria.backend.catalog.dtos.CategoryDto;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryService {
    /**
     * Crea una nueva categoría en el sistema.
     */
    Category createCategory(CategoryDto request);

    /**
     * Actualiza la información de una categoría existente.
     */
    Category updateCategory(Long id, CategoryDto request);

    /**
     * Busca y obtiene una categoría por su ID único.
     */
    Category getCategoryById(Long id);

    /**
     * Busca y obtiene una categoría utilizando su slug descriptivo.
     */
    Category getCategoryBySlug(String slug);

    /**
     * Obtiene una lista de categorías filtradas por su tipo (BLOG o CATALOG).
     */
    List<Category> getCategoriesByTipo(CategoryType tipo);

    /**
     * Obtiene todas las categorías registradas en el sistema.
     */
    List<Category> getAllCategories();

    /**
     * Elimina de forma lógica o física una categoría a partir de su ID.
     */
    void deleteCategory(Long id);
}
