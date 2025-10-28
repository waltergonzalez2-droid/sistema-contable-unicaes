package com.unicaes.contabilidad.controller;

import com.unicaes.contabilidad.model.Cuenta;
import com.unicaes.contabilidad.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    private final CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<Cuenta>> obtenerTodasLasCuentas() {
        return ResponseEntity.ok(cuentaService.obtenerTodasLasCuentas());
    }

    @GetMapping("/principales")
    public ResponseEntity<List<Cuenta>> obtenerCuentasPrincipales() {
        return ResponseEntity.ok(cuentaService.obtenerCuentasPrincipales());
    }

    @GetMapping("/{cuentaId}/subcuentas")
    public ResponseEntity<List<Cuenta>> obtenerSubcuentas(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(cuentaService.obtenerSubcuentas(cuentaId));
    }

    @PostMapping
    public ResponseEntity<Cuenta> crearCuenta(@Valid @RequestBody Cuenta cuenta) {
        return ResponseEntity.ok(cuentaService.crearCuenta(cuenta));
    }
}