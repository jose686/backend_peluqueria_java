package com.peluqueria.backend.catalog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.peluqueria.backend.catalog.dtos.CatalogItemRequest;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CatalogItemRepository;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import com.peluqueria.backend.media.repositories.MediaFileRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogItemServiceImplTest {
    @Mock CatalogItemRepository catalogItemRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock MediaFileRepository mediaFileRepository;
    @InjectMocks CatalogItemServiceImpl service;

    @Test
    void create_acceptsCatalogCategory() {
        CatalogItemRequest request = request(2L);
        Category category = Category.builder().id(2L).nombre("Cortes").tipo(CategoryType.CATALOGO).build();
        when(catalogItemRepository.findBySlug("corte-premium")).thenReturn(Optional.empty());
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(catalogItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var item = service.createCatalogItem(request);

        assertThat(item.getCategoria()).isSameAs(category);
        assertThat(item.getSlug()).isEqualTo("corte-premium");
    }

    @Test
    void create_rejectsBlogCategory() {
        when(catalogItemRepository.findBySlug("corte-premium")).thenReturn(Optional.empty());
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(Category.builder().id(2L).nombre("Noticias").tipo(CategoryType.BLOG).build()));

        assertThatThrownBy(() -> service.createCatalogItem(request(2L)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("tipo CATALOGO");
    }

    private CatalogItemRequest request(Long categoryId) {
        return new CatalogItemRequest("Corte Premium", null, "Descripción", new BigDecimal("25.00"), "SERVICIO", 30, null, null, categoryId, true);
    }
}
