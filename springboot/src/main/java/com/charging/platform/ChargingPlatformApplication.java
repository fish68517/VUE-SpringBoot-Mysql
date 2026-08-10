package com.charging.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.charging.platform.mapper")
@EnableScheduling
public class ChargingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChargingPlatformApplication.class, args);
    }
}
