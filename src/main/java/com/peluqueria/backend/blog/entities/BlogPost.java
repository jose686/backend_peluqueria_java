package com.peluqueria.backend.blog.entities;

import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.media.entities.MediaFile;
import com.peluqueria.backend.users.entities.UserAccount;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String titulo;

    @Column(unique = true, nullable = false)
    private String slug;

    @NotBlank
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenidoHtml;

    @Column(length = 500)
    private String resumen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portada_id")
    private MediaFile portada;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private UserAccount autor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Category categoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus estado;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.slug == null || this.slug.isBlank()) {
            this.slug = Category.slugify(this.titulo);
        } else {
            this.slug = Category.slugify(this.slug);
        }

        if (this.estado == PostStatus.PUBLICADO && this.fechaPublicacion == null) {
            this.fechaPublicacion = LocalDateTime.now();
        }
    }
}
