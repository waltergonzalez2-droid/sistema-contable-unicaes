package com.unicaes.contabilidad.repository;

import com.unicaes.contabilidad.model.PeriodoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoContableRepository extends JpaRepository<PeriodoContable, Long> {
    
    List<PeriodoContable> findByEstadoOrderByAñoDescMesDesc(String estado);
    
    Optional<PeriodoContable> findByAñoAndMes(Integer año, Integer mes);
    
    @Query("SELECT p FROM PeriodoContable p ORDER BY p.año DESC, p.mes DESC")
    List<PeriodoContable> findAllOrderByAñoDescMesDesc();
    
    @Query("SELECT p FROM PeriodoContable p WHERE p.estado = 'ABIERTO' ORDER BY p.año DESC, p.mes DESC")
    Optional<PeriodoContable> findPeriodoAbierto();
}
