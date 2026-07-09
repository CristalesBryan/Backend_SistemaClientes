package com.gestor.suscripciones.dto;

import com.gestor.suscripciones.model.EstadoRenovacion;
import com.gestor.suscripciones.model.MetodoPago;
import com.gestor.suscripciones.model.Suscriptor;
import com.gestor.suscripciones.model.TiempoServicio;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder
public class SuscriptorResponse {

    private Long id;
    private String usuarioTelegram;
    private String tipoServicio;
    private String descripcion;
    private TiempoServicio tiempoServicio;
    private String tiempoServicioEtiqueta;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private EstadoRenovacion estadoRenovacion;
    private MetodoPago metodoPago;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private String notas;
    private boolean contactado;
    private Long diasRestantes;
    private boolean vencido;
    private boolean porVencer;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public static SuscriptorResponse fromEntity(Suscriptor s) {
        LocalDate hoy = LocalDate.now();
        long dias = ChronoUnit.DAYS.between(hoy, s.getFechaVencimiento());
        boolean vencido = s.getFechaVencimiento().isBefore(hoy);
        boolean porVencer = !vencido && dias <= 7;

        return SuscriptorResponse.builder()
                .id(s.getId())
                .usuarioTelegram(s.getUsuarioTelegram())
                .tipoServicio(s.getTipoServicio())
                .descripcion(s.getDescripcion())
                .tiempoServicio(s.getTiempoServicio())
                .tiempoServicioEtiqueta(s.getTiempoServicio().getEtiqueta())
                .fechaInicio(s.getFechaInicio())
                .fechaVencimiento(s.getFechaVencimiento())
                .estadoRenovacion(s.getEstadoRenovacion())
                .metodoPago(s.getMetodoPago())
                .montoPagado(s.getMontoPagado())
                .fechaPago(s.getFechaPago())
                .notas(s.getNotas())
                .contactado(s.isContactado())
                .diasRestantes(dias)
                .vencido(vencido)
                .porVencer(porVencer)
                .creadoEn(s.getCreadoEn())
                .actualizadoEn(s.getActualizadoEn())
                .build();
    }
}
