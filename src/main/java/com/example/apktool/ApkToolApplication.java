package com.example.apktool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.apktool.dao")
public class ApkToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApkToolApplication.class, args);
    }
}
