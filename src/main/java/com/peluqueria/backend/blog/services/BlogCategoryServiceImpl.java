package com.peluqueria.backend.blog.services;

import com.peluqueria.backend.blog.repositories.BlogPostRepository;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogCategoryServiceImpl implements BlogCategoryService {
    private final CategoryRepository categoryRepository;
    private final BlogPostRepository blogPostRepository;

    public BlogCategoryServiceImpl(CategoryRepository categoryRepository, BlogPostRepository blogPostRepository) {
        this.categoryRepository = categoryRepository;
        this.blogPostRepository = blogPostRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findByTipo(CategoryType.BLOG);
    }

    @Override
    @Transactional
    public Category create(String nombre) {
        String slug = Category.slugify(nombre);
        if (categoryRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }
        return categoryRepository.save(Category.builder().nombre(nombre.trim()).tipo(CategoryType.BLOG).build());
    }

    @Override
    @Transactional
    public Category update(Long id, String nombre) {
        Category category = findBlogCategory(id);
        String slug = Category.slugify(nombre);
        if (!category.getSlug().equals(slug) && categoryRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }
        category.setNombre(nombre.trim());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findBlogCategory(id);
        if (!blogPostRepository.findByCategoriaId(id).isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar una categoría con artículos asociados.");
        }
        categoryRepository.delete(category);
    }

    private Category findBlogCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));
        if (category.getTipo() != CategoryType.BLOG) {
            throw new IllegalArgumentException("La categoría no pertenece al blog.");
        }
        return category;
    }
}
