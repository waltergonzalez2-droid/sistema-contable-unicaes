package com.unicaes.contabilidad.repository;

import com.unicaes.contabilidad.model.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface AsientoContableRepository extends JpaRepository<AsientoContable, Long> {
    List<AsientoContable> findByFechaBetweenOrderByFechaDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<AsientoContable> findByEstadoOrderByFechaDesc(String estado);
    
    @Query("SELECT a FROM AsientoContable a WHERE a.fecha >= ?1 AND a.fecha <= ?2 AND a.estado = 'REGISTRADO'")
    List<AsientoContable> findAsientosRegistradosPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}