package com.gestor.suscripciones.dto;

import com.gestor.suscripciones.model.EstadoRenovacion;
import com.gestor.suscripciones.model.MetodoPago;
import com.gestor.suscripciones.model.TiempoServicio;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SuscriptorRequest {

    @NotBlank(message = "El usuario de Telegram es obligatorio")
    @Pattern(regexp = "^@?[a-zA-Z0-9_]{5,32}$", message = "Usuario de Telegram inválido")
    private String usuarioTelegram;

    @NotBlank(message = "El tipo de servicio es obligatorio")
    private String tipoServicio;

    private String descripcion;

    @NotNull(message = "El tiempo de servicio es obligatorio")
    private TiempoServicio tiempoServicio;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaVencimiento;

    private EstadoRenovacion estadoRenovacion;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal montoPagado;

    private LocalDate fechaPago;

    private String notas;
}
