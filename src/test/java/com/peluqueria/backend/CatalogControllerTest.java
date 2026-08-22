package com.peluqueria.backend;

import com.peluqueria.backend.catalog.dtos.CatalogItemRequest;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CatalogItemRepository;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import com.peluqueria.backend.blog.repositories.BlogPostRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CatalogItemRepository catalogItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Delete dependent rows before their category parents to satisfy MySQL FKs.
        blogPostRepository.deleteAll();
        catalogItemRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create a test category for the catalog
        testCategory = Category.builder()
                .nombre("Cortes de pelo")
                .tipo(CategoryType.CATALOGO)
                .build();
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void testGetCatalogItemsPublicly() throws Exception {
        mockMvc.perform(get("/api/v1/catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testCreateCatalogItemAsAdmin() throws Exception {
        CatalogItemRequest request = new CatalogItemRequest(
                "Corte Bob",
                "corte-bob-2026",
                "Un corte clásico y moderno",
                new BigDecimal("25.50"),
                "SERVICIO",
                30, // 30 minutes duration
                null, // Services do not have stock
                null, // No cover media file initially
                testCategory.getId(),
                true
        );

        mockMvc.perform(post("/api/v1/catalog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre", is("Corte Bob")))
                .andExpect(jsonPath("$.slug", is("corte-bob-2026")))
                .andExpect(jsonPath("$.precio", is(25.50)))
                .andExpect(jsonPath("$.tipo", is("SERVICIO")))
                .andExpect(jsonPath("$.duracionMinutos", is(30)))
                .andExpect(jsonPath("$.categoria.id", is(testCategory.getId().intValue())));
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = {"CLIENTE"})
    void testCreateCatalogItemAsClientForbidden() throws Exception {
        CatalogItemRequest request = new CatalogItemRequest(
                "Shampoo Anticaspa",
                "shampoo-anticaspa",
                "Para cabellos secos",
                new BigDecimal("12.99"),
                "PRODUCTO",
                null,
                10,
                null,
                testCategory.getId(),
                true
        );

        mockMvc.perform(post("/api/v1/catalog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
