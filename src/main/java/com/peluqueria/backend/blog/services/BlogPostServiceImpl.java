package com.peluqueria.backend.blog.services;

import com.peluqueria.backend.blog.dtos.BlogPostRequest;
import com.peluqueria.backend.blog.entities.BlogPost;
import com.peluqueria.backend.blog.entities.PostStatus;
import com.peluqueria.backend.blog.repositories.BlogPostRepository;
import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import com.peluqueria.backend.media.entities.MediaFile;
import com.peluqueria.backend.media.repositories.MediaFileRepository;
import com.peluqueria.backend.users.entities.UserAccount;
import com.peluqueria.backend.users.repositories.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final UserAccountRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MediaFileRepository mediaFileRepository;

    @Autowired
    public BlogPostServiceImpl(BlogPostRepository blogPostRepository,
                               UserAccountRepository userRepository,
                               CategoryRepository categoryRepository,
                               MediaFileRepository mediaFileRepository) {
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Override
    @Transactional
    public BlogPost createBlogPost(BlogPostRequest request, String autorEmail) {
        String slug = request.slug();
        if (slug == null || slug.isBlank()) {
            slug = Category.slugify(request.titulo());
        } else {
            slug = Category.slugify(slug);
        }

        if (blogPostRepository.findBySlug(slug).isPresent()) {
            throw new IllegalArgumentException("Ya existe un artículo con ese título o slug");
        }

        UserAccount autor = userRepository.findByEmail(autorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));

        Category category = categoryRepository.findById(request.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if (category.getTipo() != CategoryType.BLOG) {
            throw new IllegalArgumentException("La categoría seleccionada debe ser de tipo BLOG");
        }

        MediaFile portada = null;
        if (request.portadaId() != null) {
            portada = mediaFileRepository.findById(request.portadaId())
                    .orElseThrow(() -> new IllegalArgumentException("Archivo multimedia de portada no encontrado"));
        }

        PostStatus estado = PostStatus.valueOf(request.estado().toUpperCase());

        BlogPost post = BlogPost.builder()
                .titulo(request.titulo())
                .slug(slug)
                .contenidoHtml(request.contenidoHtml())
                .resumen(request.resumen())
                .portada(portada)
                .autor(autor)
                .categoria(category)
                .estado(estado)
                .fechaPublicacion(estado == PostStatus.PUBLICADO ? LocalDateTime.now() : null)
                .build();

        return blogPostRepository.save(post);
    }

    @Override
    @Transactional
    public BlogPost updateBlogPost(Long id, BlogPostRequest request) {
        BlogPost post = getBlogPostById(id);

        String slug = request.slug();
        if (slug == null || slug.isBlank()) {
            slug = Category.slugify(request.titulo());
        } else {
            slug = Category.slugify(slug);
        }

        if (!post.getSlug().equals(slug) && blogPostRepository.findBySlug(slug).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro artículo con ese título o slug");
        }

        Category category = categoryRepository.findById(request.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if (category.getTipo() != CategoryType.BLOG) {
            throw new IllegalArgumentException("La categoría seleccionada debe ser de tipo BLOG");
        }

        MediaFile portada = null;
        if (request.portadaId() != null) {
            portada = mediaFileRepository.findById(request.portadaId())
                    .orElseThrow(() -> new IllegalArgumentException("Archivo multimedia de portada no encontrado"));
        }

        PostStatus estado = PostStatus.valueOf(request.estado().toUpperCase());

        post.setTitulo(request.titulo());
        post.setSlug(slug);
        post.setContenidoHtml(request.contenidoHtml());
        post.setResumen(request.resumen());
        post.setPortada(portada);
        post.setCategoria(category);

        if (post.getEstado() != PostStatus.PUBLICADO && estado == PostStatus.PUBLICADO) {
            post.setFechaPublicacion(LocalDateTime.now());
        } else if (estado == PostStatus.BORRADOR) {
            post.setFechaPublicacion(null);
        }
        post.setEstado(estado);

        return blogPostRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPost> getAllBlogPosts() {
        return blogPostRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPost> getPublishedBlogPosts() {
        return blogPostRepository.findByEstado(PostStatus.PUBLICADO);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPost getBlogPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artículo de blog no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPost getBlogPostBySlug(String slug) {
        return blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Artículo de blog no encontrado con slug: " + slug));
    }

    @Override
    @Transactional
    public void deleteBlogPost(Long id) {
        BlogPost post = getBlogPostById(id);
        blogPostRepository.delete(post);
    }
}
