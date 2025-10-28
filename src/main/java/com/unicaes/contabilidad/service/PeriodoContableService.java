package com.unicaes.contabilidad.service;

import com.unicaes.contabilidad.model.PeriodoContable;
import com.unicaes.contabilidad.repository.PeriodoContableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PeriodoContableService {

    @Autowired
    private PeriodoContableRepository periodoRepository;

    public List<PeriodoContable> obtenerTodos() {
        return periodoRepository.findAllOrderByAñoDescMesDesc();
    }

    public Optional<PeriodoContable> obtenerPorId(Long id) {
        return periodoRepository.findById(id);
    }

    public PeriodoContable guardar(PeriodoContable periodo) {
        // Validar que no exista otro periodo para el mismo año/mes
        Optional<PeriodoContable> existente = periodoRepository.findByAñoAndMes(periodo.getAño(), periodo.getMes());
        if (existente.isPresent() && !existente.get().getId().equals(periodo.getId())) {
            throw new RuntimeException("Ya existe un período contable para " + periodo.getMes() + "/" + periodo.getAño());
        }

        // Si no se especifican fechas, calcularlas automáticamente
        if (periodo.getFechaInicio() == null || periodo.getFechaFin() == null) {
            YearMonth yearMonth = YearMonth.of(periodo.getAño(), periodo.getMes());
            periodo.setFechaInicio(yearMonth.atDay(1));
            periodo.setFechaFin(yearMonth.atEndOfMonth());
        }

        // Si es nuevo y no tiene estado, ponerlo como ABIERTO
        if (periodo.getId() == null && periodo.getEstado() == null) {
            periodo.setEstado("ABIERTO");
        }

        return periodoRepository.save(periodo);
    }

    public void eliminar(Long id) {
        PeriodoContable periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Período no encontrado"));
        
        if ("CERRADO".equals(periodo.getEstado())) {
            throw new RuntimeException("No se puede eliminar un período cerrado");
        }
        
        periodoRepository.deleteById(id);
    }

    public PeriodoContable cerrarPeriodo(Long id, String usuario) {
        PeriodoContable periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Período no encontrado"));
        
        if ("CERRADO".equals(periodo.getEstado())) {
            throw new RuntimeException("El período ya está cerrado");
        }
        
        periodo.setEstado("CERRADO");
        periodo.setFechaCierre(LocalDateTime.now());
        periodo.setCerradoPor(usuario);
        
        return periodoRepository.save(periodo);
    }

    public PeriodoContable reabrirPeriodo(Long id) {
        PeriodoContable periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Período no encontrado"));
        
        periodo.setEstado("ABIERTO");
        periodo.setFechaCierre(null);
        periodo.setCerradoPor(null);
        
        return periodoRepository.save(periodo);
    }

    public Optional<PeriodoContable> obtenerPeriodoAbierto() {
        return periodoRepository.findPeriodoAbierto();
    }

    public List<PeriodoContable> obtenerPorEstado(String estado) {
        return periodoRepository.findByEstadoOrderByAñoDescMesDesc(estado);
    }
}
