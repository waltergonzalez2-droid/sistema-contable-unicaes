package com.unicaes.contabilidad.repository;

import com.unicaes.contabilidad.model.DetalleAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleAsientoRepository extends JpaRepository<DetalleAsiento, Long> {
    
    List<DetalleAsiento> findByAsientoContableId(Long asientoId);
    
    List<DetalleAsiento> findByCuentaId(Long cuentaId);
}
