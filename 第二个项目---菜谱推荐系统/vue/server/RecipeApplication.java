package server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("server.mapper")
public class RecipeApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecipeApplication.class, args);
    }
} 