package com.peluqueria.backend.blog.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.peluqueria.backend.blog.entities.BlogPost;
import com.peluqueria.backend.blog.entities.PostStatus;
import com.peluqueria.backend.blog.services.BlogPostService;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.core.security.JwtAuthenticationFilter;
import com.peluqueria.backend.core.security.JwtTokenProvider;
import com.peluqueria.backend.core.security.CustomUserDetailsService;
import com.peluqueria.backend.core.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BlogPostController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class BlogPostControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean BlogPostService service;
    @MockBean JwtTokenProvider jwtTokenProvider;
    @MockBean CustomUserDetailsService customUserDetailsService;

    private static final String REQUEST = "{\"titulo\":\"Título\",\"contenidoHtml\":\"<p>Texto</p>\",\"categoriaId\":1,\"estado\":\"BORRADOR\"}";

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_withoutAdminIsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/blog").contentType(MediaType.APPLICATION_JSON).content(REQUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@test.es", roles = "ADMIN")
    void create_asAdminReturnsPost() throws Exception {
        BlogPost post = BlogPost.builder().id(3L).titulo("Título").slug("titulo").contenidoHtml("<p>Texto</p>")
                .categoria(Category.builder().id(1L).nombre("Noticias").tipo(CategoryType.BLOG).build())
                .estado(PostStatus.BORRADOR).build();
        when(service.createBlogPost(any(), eq("admin@test.es"))).thenReturn(post);

        mockMvc.perform(post("/api/v1/blog").contentType(MediaType.APPLICATION_JSON).content(REQUEST))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(3));
    }
}
