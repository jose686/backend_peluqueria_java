package com.peluqueria.backend.media.dtos;

import com.peluqueria.backend.media.entities.MediaFile;

import java.time.LocalDateTime;

public record MediaFileDto(
    Long id,
    String identificador,
    String filename,
    String fileType,
    String url,
    LocalDateTime fechaSubida
) {
    public static MediaFileDto fromEntity(MediaFile mediaFile) {
        if (mediaFile == null) return null;
        return new MediaFileDto(
            mediaFile.getId(),
            mediaFile.getIdentificador(),
            mediaFile.getFilename(),
            mediaFile.getFileType().name(),
            mediaFile.getUrl(),
            mediaFile.getFechaSubida()
        );
    }
}
