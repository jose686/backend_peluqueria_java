package com.peluqueria.backend.blog.services;

import com.peluqueria.backend.catalog.entities.Category;
import java.util.List;

public interface BlogCategoryService {
    List<Category> getAll();
    Category create(String nombre);
    Category update(Long id, String nombre);
    void delete(Long id);
}
