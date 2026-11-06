package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

/** Línea de factura con el nombre y el precio "congelados" del momento de la venta. */
@Embeddable
public class LineaFacturaEmbeddable {

    @Column(name = "codigo_producto", nullable = false, length = 20)
    private String codigoProducto;

    @Column(name = "nombre_producto", nullable = false, length = 120)
    private String nombreProducto;

    @Column(name = "precio_unitario", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private int cantidad;

    protected LineaFacturaEmbeddable() { }

    public LineaFacturaEmbeddable(String codigoProducto, String nombreProducto, BigDecimal precioUnitario, int cantidad) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public String getCodigoProducto() { return codigoProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public int getCantidad() { return cantidad; }
}
