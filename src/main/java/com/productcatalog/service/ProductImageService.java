package com.productcatalog.service;

import com.productcatalog.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class ProductImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final Path uploadDirectory;

    public ProductImageService(
            @Value("${app.upload.dir:uploads/products}") String uploadDir) {

        this.uploadDirectory = Path.of(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    public String store(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("A imagem é obrigatória.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageException(
                    "A imagem não pode ultrapassar 5 MB."
            );
        }

        try {
            byte[] content = file.getBytes();

            String extension = detectImageExtension(content);

            String filename = UUID.randomUUID() + extension;

            Files.createDirectories(uploadDirectory);

            Path destination = uploadDirectory.resolve(filename);

            Files.write(
                    destination,
                    content,
                    StandardOpenOption.CREATE_NEW
            );

            return "/uploads/products/" + filename;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível armazenar a imagem.",
                    exception
            );
        }
    }

    private String detectImageExtension(byte[] content) {

        // PNG
        if (content.length >= 8
                && content[0] == (byte) 0x89
                && content[1] == 0x50
                && content[2] == 0x4E
                && content[3] == 0x47
                && content[4] == 0x0D
                && content[5] == 0x0A
                && content[6] == 0x1A
                && content[7] == 0x0A) {

            return ".png";
        }

        // JPEG
        if (content.length >= 3
                && content[0] == (byte) 0xFF
                && content[1] == (byte) 0xD8
                && content[2] == (byte) 0xFF) {

            return ".jpg";
        }

        // WebP
        if (content.length >= 12
                && content[0] == 'R'
                && content[1] == 'I'
                && content[2] == 'F'
                && content[3] == 'F'
                && content[8] == 'W'
                && content[9] == 'E'
                && content[10] == 'B'
                && content[11] == 'P') {

            return ".webp";
        }

        throw new InvalidImageException(
                "Formato inválido. Envie uma imagem PNG, JPEG ou WebP."
        );
    }

    public void delete(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String prefix = "/uploads/products/";

        if (!imageUrl.startsWith(prefix)) {
            return;
        }

        String filename = imageUrl.substring(prefix.length());

        // Aceita somente nomes de arquivo simples.
        if (!filename.matches("[a-fA-F0-9-]+\\.(jpg|png|webp)")) {
            return;
        }

        try {
            Path file = uploadDirectory.resolve(filename).normalize();

            if (file.startsWith(uploadDirectory)) {
                Files.deleteIfExists(file);
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Não foi possível excluir a imagem.",
                    exception
            );
        }
    }
}
