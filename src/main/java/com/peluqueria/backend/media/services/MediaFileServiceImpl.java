package com.peluqueria.backend.media.services;

import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.media.entities.FileType;
import com.peluqueria.backend.media.entities.MediaFile;
import com.peluqueria.backend.media.exceptions.InvalidMediaFileException;
import com.peluqueria.backend.media.exceptions.MediaNotFoundException;
import com.peluqueria.backend.media.repositories.MediaFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Set;
import java.util.UUID;

@Service
public class MediaFileServiceImpl implements MediaFileService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final MediaFileRepository mediaFileRepository;
    private final Path fileStorageLocation;
    private final Path legacyFileStorageLocation;

    @Autowired
    public MediaFileServiceImpl(
            MediaFileRepository mediaFileRepository,
            @Value("${app.media.storage-path:uploads/media}") String storagePath) {
        this.mediaFileRepository = mediaFileRepository;
        this.fileStorageLocation = Paths.get(storagePath).toAbsolutePath().normalize();
        this.legacyFileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
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
        if (file == null || file.isEmpty()) {
            throw new InvalidMediaFileException("Debes seleccionar una imagen no vacía.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new InvalidMediaFileException("Nombre de archivo no válido.");
        }

        String extension;
        int i = originalFilename.lastIndexOf('.');
        if (i <= 0 || i == originalFilename.length() - 1) {
            throw new InvalidMediaFileException("La imagen debe tener extensión JPG, PNG o WEBP.");
        }
        extension = originalFilename.substring(i + 1).toLowerCase();
        String contentType = file.getContentType();
        if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidMediaFileException("Solo se permiten imágenes JPG, PNG o WEBP.");
        }
        String generatedName = UUID.randomUUID() + "." + extension;
        Path targetLocation = this.fileStorageLocation.resolve(generatedName).normalize();
        if (!targetLocation.getParent().equals(this.fileStorageLocation)) {
            throw new InvalidMediaFileException("Ruta de archivo no válida.");
        }

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/media/")
                    .path(generatedName)
                    .toUriString();

            // Set unique identifier
            String identificador = customIdentificador;
            if (identificador == null || identificador.isBlank()) {
                identificador = Category.slugify(originalFilename.substring(0, i)) + "-" + UUID.randomUUID().toString().substring(0, 8);
            } else {
                identificador = Category.slugify(identificador);
            }

            MediaFile mediaFile = MediaFile.builder()
                    .identificador(identificador)
                    .filename(originalFilename)
                    .storedFilename(generatedName)
                    .contentType(contentType)
                    .size(file.getSize())
                    .fileType(FileType.IMAGE)
                    .url(fileDownloadUri)
                    .build();

            try {
                return mediaFileRepository.save(mediaFile);
            } catch (RuntimeException exception) {
                Files.deleteIfExists(targetLocation);
                throw exception;
            }

        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo almacenar la imagen.", ex);
        }
    }

    /**
     * Lee un archivo como recurso físico desde el directorio de subidas.
     */
    @Transactional(readOnly = true)
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            if (!filePath.getParent().equals(this.fileStorageLocation)) {
                throw new MediaNotFoundException("Imagen no encontrada.");
            }
            if (!Files.exists(filePath) && !this.fileStorageLocation.equals(this.legacyFileStorageLocation)) {
                Path legacyPath = this.legacyFileStorageLocation.resolve(fileName).normalize();
                if (legacyPath.getParent().equals(this.legacyFileStorageLocation) && Files.exists(legacyPath)) {
                    filePath = legacyPath;
                }
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new MediaNotFoundException("Imagen no encontrada.");
            }
        } catch (MalformedURLException ex) {
            throw new MediaNotFoundException("Imagen no encontrada.");
        }
    }

    /**
     * Devuelve una lista de todos los archivos multimedia.
     */
    @Transactional(readOnly = true)
    public List<MediaFile> getAllMediaFiles() {
        return mediaFileRepository.findAll().stream()
                .sorted((left, right) -> right.getFechaSubida().compareTo(left.getFechaSubida()))
                .toList();
    }

    /**
     * Busca un MediaFile por su ID.
     */
    @Transactional(readOnly = true)
    public MediaFile getMediaFileById(Long id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Imagen no encontrada."));
    }

    /**
     * Borra el archivo del disco y elimina el registro de la base de datos.
     */
    @Transactional
    public void deleteMediaFile(Long id) {
        MediaFile mediaFile = getMediaFileById(id);
        
        // Remove physical file from disk
        String generatedName = mediaFile.getStoredFilename();
        if (generatedName == null || generatedName.isBlank()) {
            String fileDownloadUrl = mediaFile.getUrl();
            generatedName = fileDownloadUrl.substring(fileDownloadUrl.lastIndexOf('/') + 1);
        }
        Path targetLocation = this.fileStorageLocation.resolve(generatedName);
        if (!Files.exists(targetLocation) && !this.fileStorageLocation.equals(this.legacyFileStorageLocation)) {
            Path legacyPath = this.legacyFileStorageLocation.resolve(generatedName).normalize();
            if (legacyPath.getParent().equals(this.legacyFileStorageLocation) && Files.exists(legacyPath)) {
                targetLocation = legacyPath;
            }
        }
        
        try {
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo eliminar la imagen del almacenamiento.", ex);
        }

        mediaFileRepository.delete(mediaFile);
    }
}
