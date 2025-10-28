package com.unicaes.contabilidad.repository;

import com.unicaes.contabilidad.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByCodigo(String codigo);
    List<Cuenta> findByTipo(String tipo);
    List<Cuenta> findByActivoTrue();
    
    @Query("SELECT c FROM Cuenta c WHERE c.cuentaPadre IS NULL")
    List<Cuenta> findCuentasPrincipales();
    
    @Query("SELECT c FROM Cuenta c WHERE c.cuentaPadre.id = ?1")
    List<Cuenta> findSubcuentas(Long cuentaPadreId);
}