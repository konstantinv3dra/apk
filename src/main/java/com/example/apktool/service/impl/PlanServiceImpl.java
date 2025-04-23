package com.example.apktool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.apktool.dao.PlanMapper;
import com.example.apktool.entity.OssConfig;
import com.example.apktool.entity.Plan;
import com.example.apktool.entity.SignatureConfig;
import com.example.apktool.service.OssService;
import com.example.apktool.service.PlanService;
import com.example.apktool.service.SignatureService;
import com.example.apktool.utils.FileUtils;
import com.example.apktool.utils.IniParser;
import com.example.apktool.utils.ShellExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    
    private final PlanMapper planMapper;
    private final FileUtils fileUtils;
    private final ShellExecutor shellExecutor;
    private final IniParser iniParser;
    private final SignatureService signatureService;
    private final OssService ossService;
    private final ApkSigner apkSigner;
    private final PackageModifier packageModifier;
    private final OssUploader ossUploader;
    
    @Override
    public String uploadApk(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            
            if (!file.getOriginalFilename().endsWith(".apk")) {
                throw new RuntimeException("Only APK files are allowed");
            }
            
            return fileUtils.storeFile(file);
        } catch (IOException e) {
            log.error("Failed to upload APK file", e);
            throw new RuntimeException("Failed to upload APK file: " + e.getMessage());
        }
    }
    
    @Override
    public Plan createTask(Long signId, Long ossId, String configIni) {
        Plan plan = new Plan();
        plan.setSignId(signId);
        plan.setOssId(ossId);
        plan.setConfigIni(configIni);
        plan.setStatus(Plan.STATUS_PENDING);
        
        planMapper.insert(plan);
        return plan;
    }
    
    @Override
    public boolean startTask(Long id) {
        Plan plan = planMapper.selectById(id);
        if (plan == null) {
            throw new RuntimeException("Task not found: " + id);
        }
        
        if (!Plan.STATUS_PENDING.equals(plan.getStatus())) {
            throw new RuntimeException("Task is not in PENDING status: " + plan.getStatus());
        }
        
        plan.setStatus(Plan.STATUS_PROCESSING);
        planMapper.updateById(plan);
        
        new Thread(() -> {
            try {
                processTask(plan);
            } catch (Exception e) {
                log.error("Failed to process task: " + id, e);
                plan.setStatus(Plan.STATUS_FAILED);
                planMapper.updateById(plan);
            }
        }).start();
        
        return true;
    }
    
    @Override
    public Plan getTaskById(Long id) {
        return planMapper.selectById(id);
    }
    
    @Override
    public List<Plan> getAllTasks() {
        return planMapper.selectList(new QueryWrapper<>());
    }
    
    /**
     * Process an APK task
     */
    private void processTask(Plan plan) throws IOException, InterruptedException {
        log.info("Processing task: {}", plan.getId());
        
        SignatureConfig signatureConfig = signatureService.getSignatureById(plan.getSignId());
        OssConfig ossConfig = ossService.getOssConfigById(plan.getOssId());
        
        if (signatureConfig == null) {
            throw new RuntimeException("Signature configuration not found: " + plan.getSignId());
        }
        
        if (ossConfig == null) {
            throw new RuntimeException("OSS configuration not found: " + plan.getOssId());
        }
        
        Map<String, Map<String, String>> config = iniParser.parse(plan.getConfigIni());
        String newPackageName = config.getOrDefault("app", Map.of()).getOrDefault("package", "");
        
        if (newPackageName.isEmpty()) {
            throw new RuntimeException("New package name not specified in config.ini");
        }
        
        String workDir = createWorkingDirectory();
        
        try {
            String decompileDir = decompileApk(plan.getFilePath(), workDir);
            
            packageModifier.modifyPackageName(decompileDir, newPackageName);
            
            String recompiledApk = recompileApk(decompileDir, workDir);
            
            String signedApk = apkSigner.signApk(recompiledApk, signatureConfig);
            
            String downloadUrl = ossUploader.uploadFile(signedApk, ossConfig);
            
            plan.setResultUrl(downloadUrl);
            plan.setStatus(Plan.STATUS_COMPLETED);
            planMapper.updateById(plan);
            
            log.info("Task completed successfully: {}", plan.getId());
        } catch (Exception e) {
            log.error("Failed to process task: " + plan.getId(), e);
            plan.setStatus(Plan.STATUS_FAILED);
            planMapper.updateById(plan);
            throw e;
        }
    }
    
    /**
     * Create a working directory for the task
     */
    private String createWorkingDirectory() throws IOException {
        String workDir = "work_" + UUID.randomUUID().toString();
        fileUtils.createDirectory(workDir);
        return workDir;
    }
    
    /**
     * Decompile an APK file using apktool
     */
    private String decompileApk(String apkPath, String workDir) throws IOException, InterruptedException {
        log.info("Decompiling APK: {}", apkPath);
        
        String outputDir = Paths.get(workDir, "decompiled").toString();
        fileUtils.createDirectory(outputDir);
        
        String command = String.format("apktool d %s -o %s -f", apkPath, outputDir);
        shellExecutor.execute(command);
        
        return outputDir;
    }
    
    /**
     * Recompile an APK from decompiled files
     */
    private String recompileApk(String decompileDir, String workDir) throws IOException, InterruptedException {
        log.info("Recompiling APK from: {}", decompileDir);
        
        String outputApk = Paths.get(workDir, "recompiled.apk").toString();
        String command = String.format("apktool b %s -o %s", decompileDir, outputApk);
        shellExecutor.execute(command);
        
        return outputApk;
    }
}
