package com.unicaes.contabilidad.controller;

import com.unicaes.contabilidad.model.AsientoContable;
import com.unicaes.contabilidad.service.AsientoContableService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/asientos")
@RequiredArgsConstructor
public class AsientoContableController {
    private final AsientoContableService asientoService;

    @PostMapping
    public ResponseEntity<AsientoContable> crearAsiento(@Valid @RequestBody AsientoContable asiento) {
        return ResponseEntity.ok(asientoService.crearAsiento(asiento));
    }

    @PostMapping("/{id}/registrar")
    public ResponseEntity<AsientoContable> registrarAsiento(@PathVariable Long id) {
        return ResponseEntity.ok(asientoService.registrarAsiento(id));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<AsientoContable>> obtenerAsientosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(asientoService.obtenerAsientosPorPeriodo(fechaInicio, fechaFin));
    }
}