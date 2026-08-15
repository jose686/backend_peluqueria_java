package com.peluqueria.backend.blog.dtos;

import com.peluqueria.backend.blog.entities.BlogPost;
import com.peluqueria.backend.catalog.dtos.CategoryDto;
import com.peluqueria.backend.media.dtos.MediaFileDto;
import com.peluqueria.backend.users.dtos.UserDto;


import java.time.LocalDateTime;

public record BlogPostDto(
    Long id,
    String titulo,
    String slug,
    String contenidoHtml,
    String resumen,
    MediaFileDto portada,
    UserDto autor,
    CategoryDto categoria,
    String estado,
    LocalDateTime fechaPublicacion
) {
    public static BlogPostDto fromEntity(BlogPost post) {
        if (post == null) return null;
        return new BlogPostDto(
            post.getId(),
            post.getTitulo(),
            post.getSlug(),
            post.getContenidoHtml(),
            post.getResumen(),
            MediaFileDto.fromEntity(post.getPortada()),
            UserDto.fromEntity(post.getAutor()),
            CategoryDto.fromEntity(post.getCategoria()),
            post.getEstado().name(),
            post.getFechaPublicacion()
        );
    }
}
