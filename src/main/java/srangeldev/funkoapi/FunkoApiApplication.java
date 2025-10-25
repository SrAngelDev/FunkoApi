package srangeldev.funkoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Anotación para habilitar la caché de Spring
@EnableCaching
@SpringBootApplication
//Anotación IMPORTANTE para activar el sistema de auditoria de Spring para que detecte
// anotaciones como @CreatedDate o @LastModified
@EnableJpaAuditing
public class FunkoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunkoApiApplication.class, args);
    }

}
