package com.gestor.suscripciones.util;

import com.gestor.suscripciones.model.TiempoServicio;

import java.time.LocalDate;

public final class FechaVencimientoCalculator {

    private FechaVencimientoCalculator() {}

    public static LocalDate calcular(LocalDate fechaInicio, TiempoServicio tiempoServicio, LocalDate fechaVencimientoManual) {
        if (tiempoServicio == TiempoServicio.PERSONALIZADO) {
            if (fechaVencimientoManual == null) {
                throw new IllegalArgumentException("La fecha de vencimiento es obligatoria para tiempo personalizado");
            }
            return fechaVencimientoManual;
        }
        return fechaInicio.plusDays(tiempoServicio.getDias());
    }
}
