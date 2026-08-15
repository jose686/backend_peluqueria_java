package com.peluqueria.backend.blog.services;

import com.peluqueria.backend.blog.dtos.BlogPostRequest;
import com.peluqueria.backend.blog.entities.BlogPost;

import java.util.List;

public interface BlogPostService {
    /**
     * Crea una nueva entrada en el blog asociada a un autor.
     */
    BlogPost createBlogPost(BlogPostRequest request, String autorEmail);

    /**
     * Actualiza los datos de una entrada de blog existente.
     */
    BlogPost updateBlogPost(Long id, BlogPostRequest request);

    /**
     * Recupera todas las entradas de blog registradas (incluyendo borradores).
     */
    List<BlogPost> getAllBlogPosts();

    /**
     * Recupera solo aquellas entradas de blog que tengan el estado de PUBLICADO.
     */
    List<BlogPost> getPublishedBlogPosts();

    /**
     * Busca una entrada de blog por su identificador único numérico.
     */
    BlogPost getBlogPostById(Long id);

    /**
     * Busca una entrada de blog a través de su slug único.
     */
    BlogPost getBlogPostBySlug(String slug);

    /**
     * Elimina permanentemente una entrada de blog por su ID.
     */
    void deleteBlogPost(Long id);
}
