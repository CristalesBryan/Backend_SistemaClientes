package com.gestor.suscripciones.scheduler;

import com.gestor.suscripciones.service.SuscriptorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VencimientoScheduler {

    private final SuscriptorService suscriptorService;

    @Scheduled(cron = "0 0 1 * * *")
    public void revisarVencimientos() {
        log.info("Ejecutando revisión diaria de vencimientos...");
        suscriptorService.actualizarEstadosVencidos();
        log.info("Revisión de vencimientos completada.");
    }
}
