package com.gestor.suscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SuscripcionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuscripcionesApplication.class, args);
    }
}
