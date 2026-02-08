package com.postgraduate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.postgraduate")
public class SchoolSelectionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolSelectionSystemApplication.class, args);
    }

}
