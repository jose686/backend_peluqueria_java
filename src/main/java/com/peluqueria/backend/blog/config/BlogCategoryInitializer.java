package com.peluqueria.backend.blog.config;

import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BlogCategoryInitializer implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    public BlogCategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!categoryRepository.findByTipo(CategoryType.BLOG).isEmpty()) return;
        List.of("Tendencias", "Cuidado Capilar", "Tratamientos", "Noticias").forEach(nombre ->
                categoryRepository.save(Category.builder().nombre(nombre).tipo(CategoryType.BLOG).build()));
    }
}
