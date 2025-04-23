package com.example.apktool.service;

import com.example.apktool.entity.Plan;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PlanService {
    String uploadApk(MultipartFile file);
    
    Plan createTask(Long signId, Long ossId, String configIni);
    
    boolean startTask(Long id);
    
    Plan getTaskById(Long id);
    
    List<Plan> getAllTasks();
}
