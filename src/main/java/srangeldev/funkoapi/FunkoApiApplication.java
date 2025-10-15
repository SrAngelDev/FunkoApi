package srangeldev.funkoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

// Anotación para habilitar la caché de Spring
@EnableCaching
@SpringBootApplication
public class FunkoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunkoApiApplication.class, args);
    }

}
