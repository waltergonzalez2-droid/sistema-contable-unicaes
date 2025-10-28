package com.unicaes.contabilidad.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ASIENTOS_CONTABLES")
public class AsientoContable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asiento_seq")
    @SequenceGenerator(name = "asiento_seq", sequenceName = "asiento_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 1000)
    private String descripcion;

    @OneToMany(mappedBy = "asientoContable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleAsiento> detalles = new ArrayList<>();

    @Column(nullable = false)
    private String estado; // BORRADOR, REGISTRADO, ANULADO

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "creado_por")
    private String creadoPor;

    @Column(name = "modificado_por")
    private String modificadoPor;

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