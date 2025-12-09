package com.benchmark.jersey;

import com.benchmark.jersey.config.JerseyConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Main entry point for Variant A - Jersey REST API
 */
public class Main {
    
    private static final String BASE_URI = "http://0.0.0.0:8080/";

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("=".repeat(60));
        System.out.println("REST API Benchmark - Variant A: JAX-RS (Jersey) + JPA");
        System.out.println("=".repeat(60));
        
        // Create and start HTTP server
        final HttpServer server = startServer();
        
        System.out.println("\nServer started successfully!");
        System.out.println("API Base URL: " + BASE_URI);
        System.out.println("JMX Metrics: Configure with -javaagent for Prometheus export");
        System.out.println("\nEndpoints:");
        System.out.println("  GET    /categories");
        System.out.println("  GET    /categories/{id}");
        System.out.println("  POST   /categories");
        System.out.println("  PUT    /categories/{id}");
        System.out.println("  DELETE /categories/{id}");
        System.out.println("  GET    /categories/{id}/items");
        System.out.println("  GET    /items");
        System.out.println("  GET    /items/{id}");
        System.out.println("  GET    /items?categoryId={id}");
        System.out.println("  POST   /items");
        System.out.println("  PUT    /items/{id}");
        System.out.println("  DELETE /items/{id}");
        System.out.println("\nPress CTRL+C to stop the server...");
        System.out.println("=".repeat(60));
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            server.shutdownNow();
            System.out.println("Server stopped.");
        }));
        
        // Keep server running
        Thread.currentThread().join();
    }

    private static HttpServer startServer() {
        final JerseyConfig config = new JerseyConfig();
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }
}
