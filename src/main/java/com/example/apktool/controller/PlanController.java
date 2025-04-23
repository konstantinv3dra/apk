package com.example.apktool.controller;

import com.example.apktool.entity.Plan;
import com.example.apktool.service.PlanService;
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
public class PlanController {
    
    private final PlanService planService;
    
    /**
     * Upload an APK file
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadApk(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = planService.uploadApk(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filePath", filePath);
            response.put("fileName", file.getOriginalFilename());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to upload APK", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Create a new task
     */
    @PostMapping("/task/create")
    public ResponseEntity<Map<String, Object>> createTask(
            @RequestParam("filePath") String filePath,
            @RequestParam("signId") Long signId,
            @RequestParam("ossId") Long ossId,
            @RequestParam("configIni") String configIni) {
        try {
            Plan plan = planService.createTask(signId, ossId, configIni);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("taskId", plan.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to create task", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Start a task
     */
    @PostMapping("/task/start/{id}")
    public ResponseEntity<Map<String, Object>> startTask(@PathVariable Long id) {
        try {
            boolean started = planService.startTask(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", started);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to start task", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get task status and result
     */
    @GetMapping("/task/{id}")
    public ResponseEntity<Plan> getTask(@PathVariable Long id) {
        Plan plan = planService.getTaskById(id);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plan);
    }
}
