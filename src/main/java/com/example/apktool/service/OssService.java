package com.example.apktool.service;

import com.example.apktool.entity.OssConfig;

import java.util.List;

public interface OssService {
    OssConfig addOssConfig(String type, String bucketName, String endpoint, String accessKey, String secretKey);
    
    List<OssConfig> getAllOssConfigs();
    
    OssConfig getOssConfigById(Long id);
    
    boolean deleteOssConfig(Long id);
}
