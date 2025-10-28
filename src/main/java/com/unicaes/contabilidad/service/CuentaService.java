package com.unicaes.contabilidad.service;

import com.unicaes.contabilidad.model.Cuenta;
import com.unicaes.contabilidad.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaService {
    private final CuentaRepository cuentaRepository;

    @Transactional(readOnly = true)
    public List<Cuenta> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Cuenta> obtenerCuentasActivas() {
        return cuentaRepository.findByActivoTrue();
    }
    
    @Transactional(readOnly = true)
    public Optional<Cuenta> obtenerPorId(Long id) {
        return cuentaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Cuenta> obtenerCuentasPrincipales() {
        return cuentaRepository.findCuentasPrincipales();
    }

    @Transactional(readOnly = true)
    public List<Cuenta> obtenerSubcuentas(Long cuentaPadreId) {
        return cuentaRepository.findSubcuentas(cuentaPadreId);
    }

    @Transactional
    public Cuenta crearCuenta(Cuenta cuenta) {
        cuenta.setSaldo(BigDecimal.ZERO);
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public void actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }

    @Transactional
    public void eliminarCuenta(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        cuentaRepository.delete(cuenta);
    }
}