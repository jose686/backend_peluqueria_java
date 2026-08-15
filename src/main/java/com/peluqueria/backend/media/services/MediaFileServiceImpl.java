package com.peluqueria.backend.media.services;

import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.media.entities.FileType;
import com.peluqueria.backend.media.entities.MediaFile;
import com.peluqueria.backend.media.repositories.MediaFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class MediaFileServiceImpl implements MediaFileService {

    private final MediaFileRepository mediaFileRepository;
    private final Path fileStorageLocation;

    @Autowired
    public MediaFileServiceImpl(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear la carpeta de subida de archivos.", ex);
        }
    }

    /**
     * Guarda el archivo MultipartFile en el disco y guarda el registro con identificador único.
     */
    @Transactional
    public MediaFile storeFile(MultipartFile file, String customIdentificador) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new IllegalArgumentException("Nombre de archivo no válido");
        }

        // Extract extension and generate unique name
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }
        String generatedName = UUID.randomUUID().toString() + extension;

        try {
            // Write file
            Path targetLocation = this.fileStorageLocation.resolve(generatedName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Determine FileType
            FileType fileType = FileType.IMAGE;
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("video")) {
                fileType = FileType.VIDEO;
            }

            // Create download URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/media/files/")
                    .path(generatedName)
                    .toUriString();

            // Set unique identifier
            String identificador = customIdentificador;
            if (identificador == null || identificador.isBlank()) {
                identificador = Category.slugify(originalFilename.replace(extension, "")) + "-" + UUID.randomUUID().toString().substring(0, 8);
            } else {
                identificador = Category.slugify(identificador);
            }

            MediaFile mediaFile = MediaFile.builder()
                    .identificador(identificador)
                    .filename(originalFilename)
                    .fileType(fileType)
                    .url(fileDownloadUri)
                    .build();

            return mediaFileRepository.save(mediaFile);

        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo. Error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Lee un archivo como recurso físico desde el directorio de subidas.
     */
    @Transactional(readOnly = true)
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo no encontrado: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Archivo no encontrado: " + fileName, ex);
        }
    }

    /**
     * Devuelve una lista de todos los archivos multimedia.
     */
    @Transactional(readOnly = true)
    public List<MediaFile> getAllMediaFiles() {
        return mediaFileRepository.findAll();
    }

    /**
     * Busca un MediaFile por su ID.
     */
    @Transactional(readOnly = true)
    public MediaFile getMediaFileById(Long id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Archivo multimedia no encontrado con id " + id));
    }

    /**
     * Borra el archivo del disco y elimina el registro de la base de datos.
     */
    @Transactional
    public void deleteMediaFile(Long id) {
        MediaFile mediaFile = getMediaFileById(id);
        
        // Remove physical file from disk
        String fileDownloadUrl = mediaFile.getUrl();
        String generatedName = fileDownloadUrl.substring(fileDownloadUrl.lastIndexOf('/') + 1);
        Path targetLocation = this.fileStorageLocation.resolve(generatedName);
        
        try {
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            // Log but allow DB deletion
        }

        mediaFileRepository.delete(mediaFile);
    }
}
