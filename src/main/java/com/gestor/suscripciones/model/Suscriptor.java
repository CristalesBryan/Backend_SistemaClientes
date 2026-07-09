package com.gestor.suscripciones.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "suscriptores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suscriptor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String usuarioTelegram;

    @Column(nullable = false)
    private String tipoServicio;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TiempoServicio tiempoServicio;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRenovacion estadoRenovacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado;

    private LocalDate fechaPago;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(nullable = false)
    @Builder.Default
    private boolean contactado = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();

    private LocalDateTime actualizadoEn;
}
