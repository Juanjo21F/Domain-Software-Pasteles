package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Un contador por año: así los números FAC-AAAA-NNNNNN son consecutivos y no se repiten. */
@Entity
@Table(name = "secuencia_factura")
public class SecuenciaFacturaEntity {

    @Id
    private int anio;

    private long ultimo;

    protected SecuenciaFacturaEntity() { }

    public SecuenciaFacturaEntity(int anio, long ultimo) {
        this.anio = anio;
        this.ultimo = ultimo;
    }

    public int getAnio() { return anio; }
    public long getUltimo() { return ultimo; }
    public void setUltimo(long ultimo) { this.ultimo = ultimo; }
}
