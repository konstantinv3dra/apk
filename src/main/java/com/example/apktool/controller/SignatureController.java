package com.example.apktool.controller;

import com.example.apktool.entity.SignatureConfig;
import com.example.apktool.service.SignatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignatureController {
    
    private final SignatureService signatureService;
    
    /**
     * Add a new signature configuration
     */
    @PostMapping("/signature")
    public ResponseEntity<Map<String, Object>> addSignature(
            @RequestParam("name") String name,
            @RequestParam("keystoreFile") MultipartFile keystoreFile,
            @RequestParam("alias") String alias,
            @RequestParam("password") String password,
            @RequestParam("keyPassword") String keyPassword) {
        try {
            SignatureConfig config = signatureService.addSignature(name, keystoreFile, alias, password, keyPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", config.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to add signature configuration", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get all signature configurations
     */
    @GetMapping("/signatures")
    public ResponseEntity<List<SignatureConfig>> getAllSignatures() {
        List<SignatureConfig> configs = signatureService.getAllSignatures();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * Delete a signature configuration
     */
    @DeleteMapping("/signature/{id}")
    public ResponseEntity<Map<String, Object>> deleteSignature(@PathVariable Long id) {
        boolean deleted = signatureService.deleteSignature(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        
        return ResponseEntity.ok(response);
    }
}
