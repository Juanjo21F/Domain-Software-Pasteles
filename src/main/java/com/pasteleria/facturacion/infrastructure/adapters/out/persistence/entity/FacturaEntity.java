package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factura", indexes = @Index(name = "idx_factura_fecha", columnList = "fecha_emision"))
public class FacturaEntity {

    @Id
    @Column(length = 20)
    private String numero;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "cliente_documento", nullable = false, length = 20)
    private String clienteDocumento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoFacturaDb estado;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "factura_linea", joinColumns = @JoinColumn(name = "factura_numero"))
    @OrderColumn(name = "posicion")
    private List<LineaFacturaEmbeddable> lineas = new ArrayList<>();

    protected FacturaEntity() { }

    public FacturaEntity(String numero, LocalDateTime fechaEmision, String clienteDocumento,
                         EstadoFacturaDb estado, List<LineaFacturaEmbeddable> lineas) {
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.clienteDocumento = clienteDocumento;
        this.estado = estado;
        this.lineas = new ArrayList<>(lineas);
    }

    public String getNumero() { return numero; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public String getClienteDocumento() { return clienteDocumento; }
    public EstadoFacturaDb getEstado() { return estado; }
    public void setEstado(EstadoFacturaDb estado) { this.estado = estado; }
    public List<LineaFacturaEmbeddable> getLineas() { return lineas; }

    /** Espejo del enum de dominio: la BD no depende de una clase del dominio. */
    public enum EstadoFacturaDb { EMITIDA, ANULADA }
}
