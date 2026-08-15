package com.peluqueria.backend.media.controllers;

import com.peluqueria.backend.media.dtos.MediaFileDto;
import com.peluqueria.backend.media.entities.MediaFile;
import com.peluqueria.backend.media.services.MediaFileService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/media")
public class MediaFileController {

    private final MediaFileService mediaFileService;

    @Autowired
    public MediaFileController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    /**
     * Endpoint para subir un nuevo archivo al sistema (sólo accesible para administradores).
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "identificador", required = false) String identificador) {
        try {
            MediaFile mediaFile = mediaFileService.storeFile(file, identificador);
            return ResponseEntity.ok(MediaFileDto.fromEntity(mediaFile));
        } catch (Exception ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para servir/descargar un archivo físico a través de su nombre.
     */
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) {
        Resource resource = mediaFileService.loadFileAsResource(filename);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Default to octet-stream
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Endpoint para listar todos los archivos multimedia subidos (sólo accesible para administradores).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MediaFileDto>> getAllMedia() {
        List<MediaFileDto> media = mediaFileService.getAllMediaFiles().stream()
                .map(MediaFileDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(media);
    }

    /**
     * Endpoint para consultar detalles de un archivo por su ID (sólo accesible para administradores).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMediaById(@PathVariable Long id) {
        try {
            MediaFile mediaFile = mediaFileService.getMediaFileById(id);
            return ResponseEntity.ok(MediaFileDto.fromEntity(mediaFile));
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Endpoint para eliminar permanentemente un archivo del sistema (sólo accesible para administradores).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMedia(@PathVariable Long id) {
        try {
            mediaFileService.deleteMediaFile(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Archivo multimedia eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}
