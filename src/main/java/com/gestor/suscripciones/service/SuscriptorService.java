package com.gestor.suscripciones.service;

import com.gestor.suscripciones.dto.DashboardResumen;
import com.gestor.suscripciones.dto.SuscriptorRequest;
import com.gestor.suscripciones.dto.SuscriptorResponse;
import com.gestor.suscripciones.exception.ResourceNotFoundException;
import com.gestor.suscripciones.model.EstadoRenovacion;
import com.gestor.suscripciones.model.Suscriptor;
import com.gestor.suscripciones.model.TiempoServicio;
import com.gestor.suscripciones.repository.SuscriptorRepository;
import com.gestor.suscripciones.specification.SuscriptorSpecification;
import com.gestor.suscripciones.util.FechaVencimientoCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuscriptorService {

    private final SuscriptorRepository repository;

    @Transactional
    public SuscriptorResponse crear(SuscriptorRequest request) {
        Suscriptor suscriptor = mapRequestToEntity(new Suscriptor(), request);
        suscriptor.setEstadoRenovacion(
                request.getEstadoRenovacion() != null ? request.getEstadoRenovacion() : EstadoRenovacion.RENOVADO
        );
        suscriptor.setCreadoEn(LocalDateTime.now());
        return SuscriptorResponse.fromEntity(repository.save(suscriptor));
    }

    @Transactional(readOnly = true)
    public List<SuscriptorResponse> listar(String usuario, String filtro, String tipoServicio, String orden) {
        Specification<Suscriptor> spec = SuscriptorSpecification.conFiltros(usuario, filtro, tipoServicio);
        Sort sort = "asc".equalsIgnoreCase(orden)
                ? Sort.by("fechaVencimiento").ascending()
                : Sort.by("fechaVencimiento").descending();
        return repository.findAll(spec, sort).stream()
                .map(SuscriptorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SuscriptorResponse obtenerPorId(Long id) {
        return SuscriptorResponse.fromEntity(buscarEntidad(id));
    }

    @Transactional
    public SuscriptorResponse actualizar(Long id, SuscriptorRequest request) {
        Suscriptor suscriptor = buscarEntidad(id);
        mapRequestToEntity(suscriptor, request);
        if (request.getEstadoRenovacion() != null) {
            suscriptor.setEstadoRenovacion(request.getEstadoRenovacion());
        }
        suscriptor.setActualizadoEn(LocalDateTime.now());
        return SuscriptorResponse.fromEntity(repository.save(suscriptor));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Suscriptor no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public SuscriptorResponse renovar(Long id) {
        Suscriptor suscriptor = buscarEntidad(id);
        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicioAnterior = suscriptor.getFechaInicio();
        suscriptor.setFechaInicio(hoy);
        if (suscriptor.getTiempoServicio() == TiempoServicio.PERSONALIZADO) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(
                    fechaInicioAnterior, suscriptor.getFechaVencimiento());
            if (dias <= 0) dias = 30;
            suscriptor.setFechaVencimiento(hoy.plusDays(dias));
        } else {
            suscriptor.setFechaVencimiento(
                    FechaVencimientoCalculator.calcular(hoy, suscriptor.getTiempoServicio(), null)
            );
        }
        suscriptor.setEstadoRenovacion(EstadoRenovacion.RENOVADO);
        suscriptor.setFechaPago(hoy);
        suscriptor.setContactado(false);
        suscriptor.setActualizadoEn(LocalDateTime.now());
        return SuscriptorResponse.fromEntity(repository.save(suscriptor));
    }

    @Transactional
    public SuscriptorResponse noRenovar(Long id) {
        Suscriptor suscriptor = buscarEntidad(id);
        suscriptor.setEstadoRenovacion(EstadoRenovacion.NO_RENOVADO);
        suscriptor.setActualizadoEn(LocalDateTime.now());
        return SuscriptorResponse.fromEntity(repository.save(suscriptor));
    }

    @Transactional
    public SuscriptorResponse marcarContactado(Long id) {
        Suscriptor suscriptor = buscarEntidad(id);
        suscriptor.setContactado(true);
        suscriptor.setActualizadoEn(LocalDateTime.now());
        return SuscriptorResponse.fromEntity(repository.save(suscriptor));
    }

    @Transactional(readOnly = true)
    public List<SuscriptorResponse> proximosAVencer(int dias) {
        LocalDate hoy = LocalDate.now();
        return repository.findProximosAVencer(hoy, hoy.plusDays(dias)).stream()
                .map(SuscriptorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SuscriptorResponse> vencidos() {
        return repository.findVencidos(LocalDate.now()).stream()
                .map(SuscriptorResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DashboardResumen obtenerResumenDashboard() {
        LocalDate hoy = LocalDate.now();
        LocalDate finSemana = hoy.plusDays(7);
        YearMonth mesActual = YearMonth.now();

        Map<String, Long> porEstado = new LinkedHashMap<>();
        for (EstadoRenovacion estado : EstadoRenovacion.values()) {
            long count = repository.findAll().stream()
                    .filter(s -> s.getEstadoRenovacion() == estado)
                    .count();
            porEstado.put(estado.name(), count);
        }

        List<SuscriptorResponse> proximos = repository.findProximosAVencer(hoy, finSemana).stream()
                .limit(5)
                .map(SuscriptorResponse::fromEntity)
                .collect(Collectors.toList());

        return DashboardResumen.builder()
                .totalActivos(repository.countActivos(hoy, EstadoRenovacion.NO_RENOVADO))
                .vencenEstaSemana(repository.countVencenEstaSemana(hoy, finSemana))
                .vencidosSinRenovar(repository.countVencidosSinRenovar(hoy, EstadoRenovacion.RENOVADO))
                .ingresosDelMes(repository.sumIngresosDelMes(mesActual.atDay(1), mesActual.atEndOfMonth()))
                .suscripcionesPorEstado(porEstado)
                .proximosAVencer(proximos)
                .build();
    }

    @Transactional(readOnly = true)
    public List<String> listarTiposServicio() {
        return repository.findDistinctTiposServicio();
    }

    @Transactional
    public void actualizarEstadosVencidos() {
        LocalDate hoy = LocalDate.now();
        List<Suscriptor> candidatos = repository.findParaActualizarEstado(
                hoy, List.of(EstadoRenovacion.RENOVADO, EstadoRenovacion.NO_RENOVADO)
        );
        for (Suscriptor s : candidatos) {
            if (s.getEstadoRenovacion() != EstadoRenovacion.PENDIENTE) {
                s.setEstadoRenovacion(EstadoRenovacion.PENDIENTE);
                s.setActualizadoEn(LocalDateTime.now());
            }
        }
        repository.saveAll(candidatos);
    }

    private Suscriptor buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suscriptor no encontrado con id: " + id));
    }

    private Suscriptor mapRequestToEntity(Suscriptor suscriptor, SuscriptorRequest request) {
        String usuario = request.getUsuarioTelegram().trim();
        if (!usuario.startsWith("@")) {
            usuario = "@" + usuario;
        }

        suscriptor.setUsuarioTelegram(usuario);
        suscriptor.setTipoServicio(request.getTipoServicio().trim());
        suscriptor.setDescripcion(request.getDescripcion());
        suscriptor.setTiempoServicio(request.getTiempoServicio());
        suscriptor.setFechaInicio(request.getFechaInicio());
        suscriptor.setFechaVencimiento(
                FechaVencimientoCalculator.calcular(
                        request.getFechaInicio(),
                        request.getTiempoServicio(),
                        request.getFechaVencimiento()
                )
        );
        suscriptor.setMetodoPago(request.getMetodoPago());
        suscriptor.setMontoPagado(request.getMontoPagado());
        suscriptor.setFechaPago(request.getFechaPago() != null ? request.getFechaPago() : request.getFechaInicio());
        suscriptor.setNotas(request.getNotas());
        return suscriptor;
    }
}
