package com.gestor.suscripciones.controller;

import com.gestor.suscripciones.dto.DashboardResumen;
import com.gestor.suscripciones.service.SuscriptorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final SuscriptorService service;

    @GetMapping("/resumen")
    public DashboardResumen resumen() {
        return service.obtenerResumenDashboard();
    }
}
