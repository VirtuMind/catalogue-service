package com.marketplace.catalogue.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final String uploadDir = "uploads";

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetPath = Paths.get(uploadDir).resolve(filename).normalize();

            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Simulate returning a public URL (in real world you'd return an actual S3 or CDN URL)
            return "/files/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }
}
