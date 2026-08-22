package com.peluqueria.backend.media.dtos;

import com.peluqueria.backend.media.entities.MediaFile;

import java.time.LocalDateTime;

public record MediaFileDto(
    Long id,
    String identificador,
    String filename,
    String storedFilename,
    String contentType,
    Long size,
    String fileType,
    String url,
    LocalDateTime fechaSubida
) {
    public static MediaFileDto fromEntity(MediaFile mediaFile) {
        if (mediaFile == null) return null;
        String storedFilename = mediaFile.getStoredFilename();
        String mediaUrl = mediaFile.getUrl();
        if ((mediaUrl == null || mediaUrl.isBlank()) && storedFilename != null && !storedFilename.isBlank()) {
            mediaUrl = "/api/media/" + storedFilename;
        }
        return new MediaFileDto(
            mediaFile.getId(),
            mediaFile.getIdentificador(),
            mediaFile.getFilename(),
            storedFilename,
            mediaFile.getContentType(),
            mediaFile.getSize(),
            mediaFile.getFileType().name(),
            mediaUrl,
            mediaFile.getFechaSubida()
        );
    }
}
