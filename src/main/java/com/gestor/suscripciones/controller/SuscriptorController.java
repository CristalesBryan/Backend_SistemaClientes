package com.gestor.suscripciones.controller;

import com.gestor.suscripciones.dto.SuscriptorRequest;
import com.gestor.suscripciones.dto.SuscriptorResponse;
import com.gestor.suscripciones.service.SuscriptorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suscriptores")
@RequiredArgsConstructor
public class SuscriptorController {

    private final SuscriptorService service;

    @PostMapping
    public ResponseEntity<SuscriptorResponse> crear(@Valid @RequestBody SuscriptorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping
    public List<SuscriptorResponse> listar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String tipoServicio,
            @RequestParam(defaultValue = "asc") String orden
    ) {
        return service.listar(usuario, filtro, tipoServicio, orden);
    }

    @GetMapping("/proximos-vencer")
    public List<SuscriptorResponse> proximosAVencer(@RequestParam(defaultValue = "7") int dias) {
        return service.proximosAVencer(dias);
    }

    @GetMapping("/vencidos")
    public List<SuscriptorResponse> vencidos() {
        return service.vencidos();
    }

    @GetMapping("/tipos-servicio")
    public List<String> tiposServicio() {
        return service.listarTiposServicio();
    }

    @GetMapping("/{id}")
    public SuscriptorResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public SuscriptorResponse actualizar(@PathVariable Long id, @Valid @RequestBody SuscriptorRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/renovar")
    public SuscriptorResponse renovar(@PathVariable Long id) {
        return service.renovar(id);
    }

    @PutMapping("/{id}/no-renovar")
    public SuscriptorResponse noRenovar(@PathVariable Long id) {
        return service.noRenovar(id);
    }

    @PutMapping("/{id}/contactado")
    public SuscriptorResponse marcarContactado(@PathVariable Long id) {
        return service.marcarContactado(id);
    }
}
