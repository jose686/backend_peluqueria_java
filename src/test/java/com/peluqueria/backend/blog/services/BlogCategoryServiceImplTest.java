package com.peluqueria.backend.blog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.peluqueria.backend.blog.repositories.BlogPostRepository;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BlogCategoryServiceImplTest {
    @Mock CategoryRepository categoryRepository;
    @Mock BlogPostRepository blogPostRepository;
    @InjectMocks BlogCategoryServiceImpl service;

    @Test
    void getAll_returnsOnlyBlogCategories() {
        Category category = Category.builder().id(1L).nombre("Tendencias").tipo(CategoryType.BLOG).build();
        when(categoryRepository.findByTipo(CategoryType.BLOG)).thenReturn(List.of(category));

        assertThat(service.getAll()).containsExactly(category);
        verify(categoryRepository).findByTipo(CategoryType.BLOG);
    }

    @Test
    void delete_rejectsCategoryWithPosts() {
        Category category = Category.builder().id(1L).nombre("Tendencias").tipo(CategoryType.BLOG).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(blogPostRepository.findByCategoriaId(1L)).thenReturn(List.of(mock(com.peluqueria.backend.blog.entities.BlogPost.class)));

        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("artículos asociados");
        verify(categoryRepository, never()).delete(any());
    }
}
