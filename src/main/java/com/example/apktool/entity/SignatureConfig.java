package com.example.apktool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("signature_config")
public class SignatureConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String keystorePath;
    
    private String alias;
    
    private String password;
    
    private String keyPassword;
    
    private Boolean enabled;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
