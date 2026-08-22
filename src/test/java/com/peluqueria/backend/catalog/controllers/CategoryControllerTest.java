package com.peluqueria.backend.catalog.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.services.CategoryService;
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

@WebMvcTest(CategoryController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class CategoryControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean CategoryService service;
    @MockBean JwtTokenProvider jwtTokenProvider;
    @MockBean CustomUserDetailsService customUserDetailsService;

    @Test
    void list_byTypeReturnsOnlyCatalogCategories() throws Exception {
        when(service.getCategoriesByTipo(CategoryType.CATALOGO)).thenReturn(List.of(Category.builder().id(2L).nombre("Cortes").tipo(CategoryType.CATALOGO).build()));
        mockMvc.perform(get("/api/v1/categories").param("type", "CATALOGO"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].tipo").value("CATALOGO"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void create_withoutAdminIsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/categories").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Cortes\",\"tipo\":\"CATALOGO\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_asAdminReturnsOk() throws Exception {
        when(service.createCategory(org.mockito.ArgumentMatchers.any())).thenReturn(Category.builder().id(2L).nombre("Cortes").tipo(CategoryType.CATALOGO).build());
        mockMvc.perform(post("/api/v1/categories").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Cortes\",\"tipo\":\"CATALOGO\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.nombre").value("Cortes"));
    }
}
