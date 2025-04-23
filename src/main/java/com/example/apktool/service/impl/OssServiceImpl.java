package com.example.apktool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.apktool.dao.OssConfigMapper;
import com.example.apktool.entity.OssConfig;
import com.example.apktool.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {
    
    private final OssConfigMapper ossConfigMapper;
    
    @Override
    public OssConfig addOssConfig(String type, String bucketName, String endpoint, String accessKey, String secretKey) {
        if (type == null || type.trim().isEmpty()) {
            throw new RuntimeException("OSS type is required");
        }
        
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new RuntimeException("Bucket name is required");
        }
        
        if (endpoint == null || endpoint.trim().isEmpty()) {
            throw new RuntimeException("Endpoint is required");
        }
        
        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new RuntimeException("Access key is required");
        }
        
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new RuntimeException("Secret key is required");
        }
        
        if (!type.equals("OSS") && !type.equals("COS")) {
            throw new RuntimeException("Unsupported OSS type: " + type);
        }
        
        OssConfig ossConfig = new OssConfig();
        ossConfig.setType(type);
        ossConfig.setBucketName(bucketName);
        ossConfig.setEndpoint(endpoint);
        ossConfig.setAccessKey(accessKey);
        ossConfig.setSecretKey(secretKey);
        
        ossConfigMapper.insert(ossConfig);
        
        return ossConfig;
    }
    
    @Override
    public List<OssConfig> getAllOssConfigs() {
        return ossConfigMapper.selectList(new QueryWrapper<>());
    }
    
    @Override
    public OssConfig getOssConfigById(Long id) {
        return ossConfigMapper.selectById(id);
    }
    
    @Override
    public boolean deleteOssConfig(Long id) {
        return ossConfigMapper.deleteById(id) > 0;
    }
}
