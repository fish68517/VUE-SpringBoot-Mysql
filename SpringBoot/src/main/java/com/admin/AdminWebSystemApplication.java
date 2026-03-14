package com.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.admin.mapper")
public class AdminWebSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminWebSystemApplication.class, args);
    }
}
