package com.peluqueria.backend.catalog.entities;

import com.peluqueria.backend.media.entities.MediaFile;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "catalog_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String slug;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CatalogType tipo;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portada_id")
    private MediaFile portada;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Category categoria;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.slug == null || this.slug.isBlank()) {
            this.slug = Category.slugify(this.nombre);
        } else {
            this.slug = Category.slugify(this.slug);
        }

        if (this.activo == null) {
            this.activo = true;
        }

        // Clean values according to type
        if (this.tipo == CatalogType.SERVICIO) {
            this.stock = null; // Services do not have stock
        } else if (this.tipo == CatalogType.PRODUCTO) {
            this.duracionMinutos = null; // Products do not have a duration
        }
    }
}
