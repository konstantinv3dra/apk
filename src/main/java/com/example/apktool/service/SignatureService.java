package com.example.apktool.service;

import com.example.apktool.entity.SignatureConfig;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SignatureService {
    SignatureConfig addSignature(String name, MultipartFile keystoreFile, String alias, String password, String keyPassword);
    
    List<SignatureConfig> getAllSignatures();
    
    SignatureConfig getSignatureById(Long id);
    
    boolean deleteSignature(Long id);
}
