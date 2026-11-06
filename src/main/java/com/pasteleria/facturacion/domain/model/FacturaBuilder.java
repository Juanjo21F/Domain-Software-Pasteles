package com.pasteleria.facturacion.domain.model;

import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Documento;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Patrón Builder (Semana 7): una factura se arma por pasos y solo se crea cuando está completa.
 * Si el mismo producto se agrega dos veces, las cantidades se suman en una sola línea.
 */
public final class FacturaBuilder {

    private NumeroFactura numero;
    private LocalDateTime fecha;
    private Documento clienteDocumento;
    private final Map<CodigoProducto, LineaFactura> lineas = new LinkedHashMap<>();

    private FacturaBuilder() { }

    public static FacturaBuilder nueva() {
        return new FacturaBuilder();
    }

    public FacturaBuilder numero(NumeroFactura numero) {
        this.numero = numero;
        return this;
    }

    public FacturaBuilder fecha(LocalDateTime fecha) {
        this.fecha = fecha;
        return this;
    }

    public FacturaBuilder cliente(Cliente cliente) {
        if (cliente == null) {
            throw new ValorInvalidoException("El cliente es obligatorio");
        }
        this.clienteDocumento = cliente.getDocumento();
        return this;
    }

    /** Toma una copia del nombre y del precio vigentes del producto en este momento. */
    public FacturaBuilder agregarLinea(Producto producto, int cantidad) {
        if (producto == null) {
            throw new ValorInvalidoException("El producto es obligatorio");
        }
        LineaFactura nueva = new LineaFactura(producto.getCodigo(), producto.getNombre(), producto.getPrecio(), cantidad);
        lineas.merge(producto.getCodigo(), nueva, (existente, otra) -> new LineaFactura(
                existente.codigoProducto(), existente.nombreProducto(), existente.precioUnitario(),
                existente.cantidad() + otra.cantidad()));
        return this;
    }

    public Factura build() {
        return new Factura(numero, fecha, clienteDocumento, new ArrayList<>(lineas.values()), EstadoFactura.EMITIDA);
    }
}
