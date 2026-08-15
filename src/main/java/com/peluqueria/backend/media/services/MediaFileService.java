package com.peluqueria.backend.media.services;

import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.media.entities.FileType;
import com.peluqueria.backend.media.entities.MediaFile;
import com.peluqueria.backend.media.repositories.MediaFileRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public interface MediaFileService {
    /**
     * Guarda físicamente un archivo en el disco y registra sus datos en la base de datos.
     */
    MediaFile storeFile(MultipartFile file, String customIdentificador);

    /**
     * Carga un archivo desde el sistema de archivos local para ser servido como recurso.
     */
    Resource loadFileAsResource(String fileName);

    /**
     * Obtiene el listado completo de todos los archivos multimedia subidos.
     */
    List<MediaFile> getAllMediaFiles();

    /**
     * Busca y devuelve la información de un archivo multimedia por su ID único.
     */
    MediaFile getMediaFileById(Long id);

    /**
     * Elimina el archivo del almacenamiento físico y su registro en la base de datos.
     */
    void deleteMediaFile(Long id);
}
