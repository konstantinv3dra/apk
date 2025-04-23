package com.example.apktool.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("oss_config")
public class OssConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String type;
    
    private String bucketName;
    
    private String endpoint;
    
    private String accessKey;
    
    private String secretKey;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
