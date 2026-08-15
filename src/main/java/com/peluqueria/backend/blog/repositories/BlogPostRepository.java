package com.peluqueria.backend.blog.repositories;

import com.peluqueria.backend.blog.entities.BlogPost;
import com.peluqueria.backend.blog.entities.PostStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Optional<BlogPost> findBySlug(String slug);
    List<BlogPost> findByEstado(PostStatus estado);
    List<BlogPost> findByCategoriaId(Long categoriaId);
}
