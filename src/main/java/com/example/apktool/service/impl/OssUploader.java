package com.example.apktool.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.example.apktool.entity.OssConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class OssUploader {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String OSS_TYPE_ALIYUN = "OSS";
    private static final String OSS_TYPE_TENCENT = "COS";
    
    /**
     * Upload a file to object storage
     *
     * @param filePath Path to the file to upload
     * @param ossConfig Object storage configuration
     * @return URL to the uploaded file
     */
    public String uploadFile(String filePath, OssConfig ossConfig) {
        log.info("Uploading file: {} to {}", filePath, ossConfig.getType());
        
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("File not found: " + filePath);
        }
        
        String objectKey = generateObjectKey(file.getName());
        
        switch (ossConfig.getType()) {
            case OSS_TYPE_ALIYUN:
                return uploadToAliyunOss(file, objectKey, ossConfig);
            case OSS_TYPE_TENCENT:
                return uploadToTencentCos(file, objectKey, ossConfig);
            default:
                throw new RuntimeException("Unsupported OSS type: " + ossConfig.getType());
        }
    }
    
    /**
     * Generate an object key for the file
     */
    private String generateObjectKey(String fileName) {
        String dayFolder = LocalDateTime.now().format(DATE_FORMAT);
        return "apk/" + dayFolder + "/" + fileName;
    }
    
    /**
     * Upload a file to Aliyun OSS
     */
    private String uploadToAliyunOss(File file, String objectKey, OssConfig ossConfig) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKey(),
                ossConfig.getSecretKey()
            );
            
            ossClient.putObject(ossConfig.getBucketName(), objectKey, file);
            
            return String.format("https://%s.%s/%s",
                ossConfig.getBucketName(),
                ossConfig.getEndpoint().replace("https://", "").replace("http://", ""),
                objectKey
            );
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    /**
     * Upload a file to Tencent COS
     */
    private String uploadToTencentCos(File file, String objectKey, OssConfig ossConfig) {
        COSClient cosClient = null;
        try {
            String region = extractRegionFromEndpoint(ossConfig.getEndpoint());
            
            COSCredentials credentials = new BasicCOSCredentials(
                ossConfig.getAccessKey(),
                ossConfig.getSecretKey()
            );
            
            ClientConfig clientConfig = new ClientConfig(new Region(region));
            cosClient = new COSClient(credentials, clientConfig);
            
            cosClient.putObject(ossConfig.getBucketName(), objectKey, file);
            
            return String.format("https://%s.cos.%s.myqcloud.com/%s",
                ossConfig.getBucketName(),
                region,
                objectKey
            );
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }
    
    /**
     * Extract region from endpoint URL
     */
    private String extractRegionFromEndpoint(String endpoint) {
        if (endpoint.contains("cos.") && endpoint.contains(".myqcloud.com")) {
            return endpoint.replace("cos.", "").replace(".myqcloud.com", "");
        }
        return endpoint;
    }
}
