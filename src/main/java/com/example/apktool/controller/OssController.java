package com.example.apktool.controller;

import com.example.apktool.entity.OssConfig;
import com.example.apktool.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OssController {
    
    private final OssService ossService;
    
    /**
     * Add a new OSS configuration
     */
    @PostMapping("/oss")
    public ResponseEntity<Map<String, Object>> addOssConfig(
            @RequestParam("type") String type,
            @RequestParam("bucketName") String bucketName,
            @RequestParam("endpoint") String endpoint,
            @RequestParam("accessKey") String accessKey,
            @RequestParam("secretKey") String secretKey) {
        try {
            OssConfig config = ossService.addOssConfig(type, bucketName, endpoint, accessKey, secretKey);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", config.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to add OSS configuration", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get all OSS configurations
     */
    @GetMapping("/oss")
    public ResponseEntity<List<OssConfig>> getAllOssConfigs() {
        List<OssConfig> configs = ossService.getAllOssConfigs();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * Delete an OSS configuration
     */
    @DeleteMapping("/oss/{id}")
    public ResponseEntity<Map<String, Object>> deleteOssConfig(@PathVariable Long id) {
        boolean deleted = ossService.deleteOssConfig(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        
        return ResponseEntity.ok(response);
    }
}
