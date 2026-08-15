package com.peluqueria.backend.media.repositories;

import com.peluqueria.backend.media.entities.MediaFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findByIdentificador(String identificador);
    Boolean existsByIdentificador(String identificador);
}
