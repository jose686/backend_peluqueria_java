package com.peluqueria.backend.blog.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.peluqueria.backend.blog.dtos.BlogPostRequest;
import com.peluqueria.backend.blog.entities.BlogPost;
import com.peluqueria.backend.blog.entities.PostStatus;
import com.peluqueria.backend.blog.repositories.BlogPostRepository;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import com.peluqueria.backend.media.repositories.MediaFileRepository;
import com.peluqueria.backend.users.entities.Role;
import com.peluqueria.backend.users.entities.UserAccount;
import com.peluqueria.backend.users.repositories.UserAccountRepository;
import jakarta.validation.Validation;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceImplTest {
    @Mock BlogPostRepository blogPostRepository;
    @Mock UserAccountRepository userRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock MediaFileRepository mediaFileRepository;
    @InjectMocks BlogPostServiceImpl service;

    @Test
    void create_sanitizesHtmlAndPersistsBlogCategory() {
        BlogPostRequest request = request("<p>Hola <strong>Aura</strong><script>alert(1)</script></p>", "Resumen");
        UserAccount author = UserAccount.builder().id(UUID.randomUUID()).email("admin@test.es").password("x").nombre("Admin").role(Role.ADMIN).activo(true).build();
        Category category = Category.builder().id(4L).nombre("Tendencias").tipo(CategoryType.BLOG).build();
        when(blogPostRepository.findBySlug("titulo-de-prueba")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("admin@test.es")).thenReturn(Optional.of(author));
        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category));
        when(blogPostRepository.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BlogPost created = service.createBlogPost(request, "admin@test.es");

        assertThat(created.getContenidoHtml()).contains("<strong>Aura</strong>").doesNotContain("script");
        assertThat(created.getCategoria()).isSameAs(category);
        assertThat(created.getEstado()).isEqualTo(PostStatus.BORRADOR);
    }

    @Test
    void create_rejectsNonBlogCategory() {
        BlogPostRequest request = request("<p>Texto</p>", "Resumen");
        when(blogPostRepository.findBySlug("titulo-de-prueba")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(UserAccount.builder().email("a@b.es").password("x").nombre("A").role(Role.ADMIN).activo(true).build()));
        when(categoryRepository.findById(4L)).thenReturn(Optional.of(Category.builder().id(4L).nombre("Catálogo").tipo(CategoryType.CATALOGO).build()));

        assertThatThrownBy(() -> service.createBlogPost(request, "a@b.es"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("tipo BLOG");
    }

    @Test
    void request_rejectsSummaryOver350Characters() {
        BlogPostRequest request = request("<p>Texto</p>", "x".repeat(351));
        var validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertThat(validator.validate(request)).anyMatch(v -> v.getMessage().contains("350 caracteres"));
    }

    private BlogPostRequest request(String html, String summary) {
        return new BlogPostRequest("Título de prueba", null, html, summary, null, 4L, "BORRADOR");
    }
}
