package com.unicaes.contabilidad.service;

import com.unicaes.contabilidad.model.AsientoContable;
import com.unicaes.contabilidad.model.DetalleAsiento;
import com.unicaes.contabilidad.repository.AsientoContableRepository;
import com.unicaes.contabilidad.repository.DetalleAsientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsientoContableService {
    private final AsientoContableRepository asientoRepository;
    @SuppressWarnings("unused")
    private final DetalleAsientoRepository detalleRepository;
    private final CuentaService cuentaService;

    @Transactional
    public AsientoContable crearAsiento(AsientoContable asiento) {
        // Generar número automático si no existe
        if (asiento.getNumero() == null || asiento.getNumero().isEmpty()) {
            asiento.setNumero(generarNumeroAsiento());
        }
        
        // Establecer fecha si no existe
        if (asiento.getFecha() == null) {
            asiento.setFecha(LocalDateTime.now());
        }
        
        // Validar asiento
        validarAsiento(asiento);
        
        // Establecer estado inicial
        asiento.setEstado("BORRADOR");
        
        // Guardar asiento
        AsientoContable asientoGuardado = asientoRepository.save(asiento);
        
        // Establecer la relación bidireccional con los detalles
        for (DetalleAsiento detalle : asiento.getDetalles()) {
            detalle.setAsientoContable(asientoGuardado);
        }
        
        return asientoGuardado;
    }
    
    public List<AsientoContable> obtenerTodos() {
        return asientoRepository.findAll();
    }
    
    public Optional<AsientoContable> obtenerPorId(Long id) {
        return asientoRepository.findById(id);
    }
    
    public AsientoContable guardar(AsientoContable asiento) {
        validarAsiento(asiento);
        return asientoRepository.save(asiento);
    }
    
    public void eliminar(Long id) {
        AsientoContable asiento = asientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));
        
        if ("REGISTRADO".equals(asiento.getEstado())) {
            throw new RuntimeException("No se puede eliminar un asiento registrado. Debe anularlo primero.");
        }
        
        asientoRepository.deleteById(id);
    }
    
    private String generarNumeroAsiento() {
        LocalDateTime now = LocalDateTime.now();
        String fecha = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = asientoRepository.count() + 1;
        return String.format("AST-%s-%04d", fecha, count);
    }

    @Transactional
    public AsientoContable registrarAsiento(Long asientoId) {
        AsientoContable asiento = asientoRepository.findById(asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));
        
        if (!"BORRADOR".equals(asiento.getEstado())) {
            throw new RuntimeException("Solo se pueden registrar asientos en estado BORRADOR");
        }

        actualizarSaldosCuentas(asiento);
        asiento.setEstado("REGISTRADO");
        return asientoRepository.save(asiento);
    }

    @Transactional
    public AsientoContable anularAsiento(Long asientoId) {
        AsientoContable asiento = asientoRepository.findById(asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));
        
        if (!"REGISTRADO".equals(asiento.getEstado())) {
            throw new RuntimeException("Solo se pueden anular asientos en estado REGISTRADO");
        }

        // Revertir los saldos de las cuentas
        for (DetalleAsiento detalle : asiento.getDetalles()) {
            BigDecimal saldoActual = detalle.getCuenta().getSaldo();
            BigDecimal nuevoSaldo;

            // Revertir el efecto del asiento (hacer lo contrario de cuando se registró)
            if ("DEUDORA".equals(detalle.getCuenta().getNaturaleza())) {
                nuevoSaldo = saldoActual.subtract(detalle.getDebe()).add(detalle.getHaber());
            } else {
                nuevoSaldo = saldoActual.subtract(detalle.getHaber()).add(detalle.getDebe());
            }

            cuentaService.actualizarSaldo(detalle.getCuenta().getId(), nuevoSaldo);
        }

        asiento.setEstado("ANULADO");
        return asientoRepository.save(asiento);
    }

    private void validarAsiento(AsientoContable asiento) {
        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;

        for (DetalleAsiento detalle : asiento.getDetalles()) {
            totalDebe = totalDebe.add(detalle.getDebe());
            totalHaber = totalHaber.add(detalle.getHaber());
        }

        if (totalDebe.compareTo(totalHaber) != 0) {
            throw new RuntimeException("El asiento no está cuadrado. Total DEBE debe ser igual al total HABER");
        }
    }

    private void actualizarSaldosCuentas(AsientoContable asiento) {
        for (DetalleAsiento detalle : asiento.getDetalles()) {
            BigDecimal saldoActual = detalle.getCuenta().getSaldo();
            BigDecimal nuevoSaldo;

            if ("DEUDORA".equals(detalle.getCuenta().getNaturaleza())) {
                nuevoSaldo = saldoActual.add(detalle.getDebe()).subtract(detalle.getHaber());
            } else {
                nuevoSaldo = saldoActual.add(detalle.getHaber()).subtract(detalle.getDebe());
            }

            cuentaService.actualizarSaldo(detalle.getCuenta().getId(), nuevoSaldo);
        }
    }

    @Transactional(readOnly = true)
    public List<AsientoContable> obtenerAsientosPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return asientoRepository.findAsientosRegistradosPorPeriodo(fechaInicio, fechaFin);
    }
}