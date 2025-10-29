package com.graduation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@MapperScan("com.graduation.mapper")
@EntityScan("com.graduation.entity") // 如果实体不在默认扫描路径下，需要这个
public class ProtectpartappApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProtectpartappApplication.class, args);
    }
} 