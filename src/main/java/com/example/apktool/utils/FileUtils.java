package com.example.apktool.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
public class FileUtils {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    /**
     * Store a file with a generated unique name
     *
     * @param file MultipartFile to store
     * @return Path to the stored file
     */
    public String storeFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = generateUniqueFileName(extension);
        String dayFolder = LocalDateTime.now().format(DATE_FORMAT);
        
        Path uploadPath = Paths.get(uploadDir, dayFolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(uniqueFileName);
        file.transferTo(filePath.toFile());
        
        return filePath.toString();
    }
    
    /**
     * Get the file extension from a filename
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
    
    /**
     * Generate a unique filename with the given extension
     */
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + extension;
    }
    
    /**
     * Create a directory
     */
    public void createDirectory(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
