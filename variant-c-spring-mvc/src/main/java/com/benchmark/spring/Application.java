package com.benchmark.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Variant C - Spring Boot MVC
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("REST API Benchmark - Variant C: Spring Boot MVC + JPA");
        System.out.println("=".repeat(60));
        
        SpringApplication.run(Application.class, args);
        
        System.out.println("\nServer started successfully!");
        System.out.println("API Base URL: http://localhost:8082");
        System.out.println("Actuator: http://localhost:8082/actuator");
        System.out.println("Metrics: http://localhost:8082/actuator/prometheus");
        System.out.println("=".repeat(60));
    }
}
