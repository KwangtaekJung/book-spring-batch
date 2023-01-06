package com.example.chapter7;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class Chapter7Application {

    public static void main(String[] args) {
        SpringApplication.run(Chapter7Application.class, args);
    }
}
