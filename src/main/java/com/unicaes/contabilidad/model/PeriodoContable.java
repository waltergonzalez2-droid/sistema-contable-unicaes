package com.unicaes.contabilidad.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PERIODOS_CONTABLES")
public class PeriodoContable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "periodo_seq")
    @SequenceGenerator(name = "periodo_seq", sequenceName = "periodo_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private Integer año;

    @Column(nullable = false)
    private Integer mes;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private String estado; // ABIERTO, CERRADO, EN_PROCESO

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "cerrado_por")
    private String cerradoPor;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}