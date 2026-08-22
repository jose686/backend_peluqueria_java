package com.peluqueria.backend.blog.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.peluqueria.backend.blog.services.BlogCategoryService;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.core.security.JwtAuthenticationFilter;
import com.peluqueria.backend.core.security.JwtTokenProvider;
import com.peluqueria.backend.core.security.CustomUserDetailsService;
import com.peluqueria.backend.core.security.SecurityConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BlogCategoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class BlogCategoryControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean BlogCategoryService service;
    @MockBean JwtTokenProvider jwtTokenProvider;
    @MockBean CustomUserDetailsService customUserDetailsService;

    @Test
    void list_isPublic() throws Exception {
        when(service.getAll()).thenReturn(List.of(Category.builder().id(1L).nombre("Noticias").tipo(CategoryType.BLOG).build()));
        mockMvc.perform(get("/api/v1/blog/categories"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Noticias"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_withoutAdminIsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/blog/categories").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Noticias\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_asAdminReturnsCreated() throws Exception {
        when(service.create("Noticias")).thenReturn(Category.builder().id(1L).nombre("Noticias").tipo(CategoryType.BLOG).build());
        mockMvc.perform(post("/api/v1/blog/categories").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Noticias\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1));
    }
}
