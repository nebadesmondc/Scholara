package com.scholara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

/**
 * Scholara Application Entry Point.
 * This is a Spring Modulith application that enforces strict module boundaries
 * between identity, content, assessment, progress, notification, and shared modules.
 */
@Modulith
@SpringBootApplication
public class ScholaraApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScholaraApplication.class, args);
    }

}
