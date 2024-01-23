package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@SpringBootApplication
public class AiBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(AiBackendApplication.class, args);
    }

}
