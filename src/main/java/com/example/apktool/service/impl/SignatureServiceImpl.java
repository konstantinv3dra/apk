package com.example.apktool.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.apktool.dao.SignatureConfigMapper;
import com.example.apktool.entity.SignatureConfig;
import com.example.apktool.service.SignatureService;
import com.example.apktool.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureServiceImpl implements SignatureService {
    
    private final SignatureConfigMapper signatureConfigMapper;
    private final FileUtils fileUtils;
    
    @Override
    public SignatureConfig addSignature(String name, MultipartFile keystoreFile, String alias, String password, String keyPassword) {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Signature name is required");
            }
            
            if (keystoreFile == null || keystoreFile.isEmpty()) {
                throw new RuntimeException("Keystore file is required");
            }
            
            if (alias == null || alias.trim().isEmpty()) {
                throw new RuntimeException("Keystore alias is required");
            }
            
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("Keystore password is required");
            }
            
            if (keyPassword == null || keyPassword.trim().isEmpty()) {
                throw new RuntimeException("Key password is required");
            }
            
            String keystorePath = fileUtils.storeFile(keystoreFile);
            
            SignatureConfig signatureConfig = new SignatureConfig();
            signatureConfig.setName(name);
            signatureConfig.setKeystorePath(keystorePath);
            signatureConfig.setAlias(alias);
            signatureConfig.setPassword(password);
            signatureConfig.setKeyPassword(keyPassword);
            signatureConfig.setEnabled(true);
            
            signatureConfigMapper.insert(signatureConfig);
            
            return signatureConfig;
        } catch (IOException e) {
            log.error("Failed to add signature configuration", e);
            throw new RuntimeException("Failed to add signature configuration: " + e.getMessage());
        }
    }
    
    @Override
    public List<SignatureConfig> getAllSignatures() {
        return signatureConfigMapper.selectList(
            new QueryWrapper<SignatureConfig>().eq("enabled", true)
        );
    }
    
    @Override
    public SignatureConfig getSignatureById(Long id) {
        return signatureConfigMapper.selectById(id);
    }
    
    @Override
    public boolean deleteSignature(Long id) {
        SignatureConfig config = signatureConfigMapper.selectById(id);
        if (config == null) {
            return false;
        }
        
        config.setEnabled(false);
        signatureConfigMapper.updateById(config);
        return true;
    }
}
