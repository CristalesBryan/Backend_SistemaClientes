package com.gestor.suscripciones.specification;

import com.gestor.suscripciones.model.EstadoRenovacion;
import com.gestor.suscripciones.model.Suscriptor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class SuscriptorSpecification {

    private SuscriptorSpecification() {}

    public static Specification<Suscriptor> conFiltros(String usuario, String filtro, String tipoServicio) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            LocalDate hoy = LocalDate.now();

            if (usuario != null && !usuario.isBlank()) {
                String term = usuario.trim().toLowerCase();
                if (!term.startsWith("@")) {
                    term = "@" + term;
                }
                predicates.add(cb.like(cb.lower(root.get("usuarioTelegram")), "%" + term + "%"));
            }

            if (tipoServicio != null && !tipoServicio.isBlank()) {
                predicates.add(cb.equal(root.get("tipoServicio"), tipoServicio));
            }

            if (filtro != null && !filtro.isBlank()) {
                switch (filtro.toLowerCase()) {
                    case "activos" -> predicates.add(cb.greaterThanOrEqualTo(root.get("fechaVencimiento"), hoy));
                    case "por-vencer" -> {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("fechaVencimiento"), hoy));
                        predicates.add(cb.lessThanOrEqualTo(root.get("fechaVencimiento"), hoy.plusDays(7)));
                    }
                    case "vencidos" -> predicates.add(cb.lessThan(root.get("fechaVencimiento"), hoy));
                    case "no-renovados" -> predicates.add(cb.equal(root.get("estadoRenovacion"), EstadoRenovacion.NO_RENOVADO));
                    default -> { /* todos */ }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
