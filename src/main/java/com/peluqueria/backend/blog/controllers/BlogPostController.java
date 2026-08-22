package com.peluqueria.backend.blog.controllers;

import com.peluqueria.backend.blog.dtos.BlogPostDto;
import com.peluqueria.backend.blog.dtos.BlogPostRequest;
import com.peluqueria.backend.blog.entities.BlogPost;
import com.peluqueria.backend.blog.services.BlogPostService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogPostController {

    private final BlogPostService blogPostService;

    @Autowired
    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    /**
     * Endpoint para crear una entrada en el blog (sólo accesible para administradores).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBlogPost(@Valid @RequestBody BlogPostRequest request, Authentication authentication) {
        try {
            String email = authentication.getName();
            BlogPost post = blogPostService.createBlogPost(request, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(BlogPostDto.fromEntity(post));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para modificar una entrada de blog (sólo accesible para administradores).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBlogPost(@PathVariable Long id, @Valid @RequestBody BlogPostRequest request) {
        try {
            BlogPost post = blogPostService.updateBlogPost(id, request);
            return ResponseEntity.ok(BlogPostDto.fromEntity(post));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para recuperar las entradas del blog (filtrando por publicadas o todas si es administrador).
     */
    @GetMapping
    public ResponseEntity<List<BlogPostDto>> getAllBlogPosts(
            @RequestParam(value = "all", defaultValue = "false") boolean all,
            Authentication authentication) {

        List<BlogPost> posts;
        if (all && authentication != null && (
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))) {
            posts = blogPostService.getAllBlogPosts();
        } else {
            posts = blogPostService.getPublishedBlogPosts();
        }

        List<BlogPostDto> dtos = posts.stream()
                .map(BlogPostDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint para obtener el detalle de una entrada de blog por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogPostById(@PathVariable Long id) {
        try {
            BlogPost post = blogPostService.getBlogPostById(id);
            return ResponseEntity.ok(BlogPostDto.fromEntity(post));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Endpoint para buscar una entrada de blog utilizando su slug.
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getBlogPostBySlug(@PathVariable String slug) {
        try {
            BlogPost post = blogPostService.getBlogPostBySlug(slug);
            return ResponseEntity.ok(BlogPostDto.fromEntity(post));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Endpoint para eliminar definitivamente una entrada de blog (sólo accesible para administradores).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBlogPost(@PathVariable Long id) {
        try {
            blogPostService.deleteBlogPost(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Artículo de blog eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}
