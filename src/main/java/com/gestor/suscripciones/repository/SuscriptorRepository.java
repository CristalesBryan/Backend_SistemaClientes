package com.gestor.suscripciones.repository;

import com.gestor.suscripciones.model.EstadoRenovacion;
import com.gestor.suscripciones.model.Suscriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SuscriptorRepository extends JpaRepository<Suscriptor, Long>, JpaSpecificationExecutor<Suscriptor> {

    @Query("SELECT s FROM Suscriptor s WHERE s.fechaVencimiento BETWEEN :hoy AND :limite ORDER BY s.fechaVencimiento ASC")
    List<Suscriptor> findProximosAVencer(@Param("hoy") LocalDate hoy, @Param("limite") LocalDate limite);

    @Query("SELECT s FROM Suscriptor s WHERE s.fechaVencimiento < :hoy ORDER BY s.fechaVencimiento ASC")
    List<Suscriptor> findVencidos(@Param("hoy") LocalDate hoy);

    @Query("SELECT COUNT(s) FROM Suscriptor s WHERE s.fechaVencimiento >= :hoy AND s.estadoRenovacion <> :noRenovado")
    long countActivos(@Param("hoy") LocalDate hoy, @Param("noRenovado") EstadoRenovacion noRenovado);

    @Query("SELECT COUNT(s) FROM Suscriptor s WHERE s.fechaVencimiento BETWEEN :hoy AND :limite")
    long countVencenEstaSemana(@Param("hoy") LocalDate hoy, @Param("limite") LocalDate limite);

    @Query("SELECT COUNT(s) FROM Suscriptor s WHERE s.fechaVencimiento < :hoy AND s.estadoRenovacion <> :renovado")
    long countVencidosSinRenovar(@Param("hoy") LocalDate hoy, @Param("renovado") EstadoRenovacion renovado);

    @Query(value = """
            SELECT COALESCE(SUM(s.monto_pagado), 0)
            FROM suscriptores s
            WHERE EXTRACT(MONTH FROM COALESCE(s.fecha_pago, s.fecha_inicio, CAST(s.creado_en AS date))) = :mes
              AND EXTRACT(YEAR FROM COALESCE(s.fecha_pago, s.fecha_inicio, CAST(s.creado_en AS date))) = :anio
            """, nativeQuery = true)
    Double calcularIngresosMes(@Param("mes") int mes, @Param("anio") int anio);

    @Query("SELECT s.tipoServicio FROM Suscriptor s GROUP BY s.tipoServicio ORDER BY s.tipoServicio")
    List<String> findDistinctTiposServicio();

    @Query("SELECT s FROM Suscriptor s WHERE s.fechaVencimiento < :hoy AND s.estadoRenovacion NOT IN (:excluidos)")
    List<Suscriptor> findParaActualizarEstado(@Param("hoy") LocalDate hoy, @Param("excluidos") List<EstadoRenovacion> excluidos);
}
