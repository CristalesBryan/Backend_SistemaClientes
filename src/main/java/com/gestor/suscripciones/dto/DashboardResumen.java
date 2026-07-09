package com.gestor.suscripciones.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResumen {

    private long totalActivos;
    private long vencenEstaSemana;
    private long vencidosSinRenovar;
    private BigDecimal ingresosDelMes;
    private Map<String, Long> suscripcionesPorEstado;
    private List<SuscriptorResponse> proximosAVencer;
}
