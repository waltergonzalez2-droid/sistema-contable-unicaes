package com.unicaes.contabilidad.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "DETALLES_ASIENTO")
public class DetalleAsiento {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detalle_asiento_seq")
    @SequenceGenerator(name = "detalle_asiento_seq", sequenceName = "detalle_asiento_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asiento_id", nullable = false)
    private AsientoContable asientoContable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @Column(precision = 19, scale = 4)
    private BigDecimal debe;

    @Column(precision = 19, scale = 4)
    private BigDecimal haber;

    @Column(length = 500)
    private String referencia;
}