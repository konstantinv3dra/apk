package com.example.apktool.service.impl;

import com.example.apktool.utils.ShellExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class PackageModifier {
    
    private final ShellExecutor shellExecutor;
    
    private static final String MANIFEST_FILE = "AndroidManifest.xml";
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package=\"([^\"]+)\"");
    
    /**
     * Modify the package name in an APK
     *
     * @param decompileDir Directory containing decompiled APK
     * @param newPackageName New package name
     * @return Original package name that was replaced
     */
    public String modifyPackageName(String decompileDir, String newPackageName) throws IOException, InterruptedException {
        log.info("Modifying package name to: {} in {}", newPackageName, decompileDir);
        
        Path manifestPath = Paths.get(decompileDir, MANIFEST_FILE);
        String originalPackage = updateManifestPackage(manifestPath, newPackageName);
        
        updateSmaliFiles(decompileDir, originalPackage, newPackageName);
        
        return originalPackage;
    }
    
    /**
     * Update the package name in AndroidManifest.xml
     */
    private String updateManifestPackage(Path manifestPath, String newPackageName) throws IOException {
        log.info("Updating package in AndroidManifest.xml: {}", manifestPath);
        String content = new String(Files.readAllBytes(manifestPath));
        
        Matcher matcher = PACKAGE_PATTERN.matcher(content);
        if (!matcher.find()) {
            throw new IOException("Could not find package attribute in AndroidManifest.xml");
        }
        
        String originalPackage = matcher.group(1);
        log.info("Original package: {}", originalPackage);
        
        String updatedContent = content.replace("package=\"" + originalPackage + "\"", "package=\"" + newPackageName + "\"");
        Files.write(manifestPath, updatedContent.getBytes());
        
        return originalPackage;
    }
    
    /**
     * Update package references in smali files
     */
    private void updateSmaliFiles(String decompileDir, String originalPackage, String newPackageName) throws IOException {
        log.info("Updating smali files with new package name");
        
        String originalPath = originalPackage.replace('.', File.separatorChar);
        String newPath = newPackageName.replace('.', File.separatorChar);
        
        File smaliDir = new File(decompileDir, "smali");
        if (!smaliDir.exists()) {
            log.warn("Smali directory not found: {}", smaliDir);
            return;
        }
        
        processSmaliDirectory(smaliDir, originalPackage, newPackageName);
        
        for (int i = 2; i <= 10; i++) {
            File smaliDexDir = new File(decompileDir, "smali_classes" + i);
            if (smaliDexDir.exists()) {
                processSmaliDirectory(smaliDexDir, originalPackage, newPackageName);
            } else {
                break;
            }
        }
        
        File originalPackageDir = new File(smaliDir, originalPath);
        if (originalPackageDir.exists()) {
            File newPackageDir = new File(smaliDir, newPath);
            if (!newPackageDir.exists()) {
                newPackageDir.mkdirs();
            }
            
            for (File file : originalPackageDir.listFiles()) {
                if (file.isFile()) {
                    Files.move(file.toPath(), new File(newPackageDir, file.getName()).toPath());
                }
            }
        }
    }
    
    /**
     * Process all smali files in a directory to update package references
     */
    private void processSmaliDirectory(File directory, String originalPackage, String newPackageName) throws IOException {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                processSmaliDirectory(file, originalPackage, newPackageName);
            } else if (file.getName().endsWith(".smali")) {
                updateSmaliFile(file, originalPackage, newPackageName);
            }
        }
    }
    
    /**
     * Update package references in a single smali file
     */
    private void updateSmaliFile(File file, String originalPackage, String newPackageName) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        String updatedContent = content.replace("L" + originalPackage.replace('.', '/'), "L" + newPackageName.replace('.', '/'));
        
        if (!content.equals(updatedContent)) {
            Files.write(file.toPath(), updatedContent.getBytes());
        }
    }
}
