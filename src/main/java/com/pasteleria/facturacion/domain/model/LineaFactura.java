package com.pasteleria.facturacion.domain.model;

import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;

/**
 * Línea de una factura. Guarda una COPIA del nombre y del precio al momento de la venta:
 * si mañana cambia el precio del producto, las facturas ya emitidas no se alteran.
 */
public record LineaFactura(CodigoProducto codigoProducto, String nombreProducto, Dinero precioUnitario, int cantidad) {

    public LineaFactura {
        if (codigoProducto == null || nombreProducto == null || precioUnitario == null) {
            throw new ValorInvalidoException("La línea de factura está incompleta");
        }
        if (cantidad <= 0) {
            throw new ValorInvalidoException("La cantidad debe ser mayor que cero");
        }
    }

    public Dinero subtotal() {
        return precioUnitario.multiplicar(cantidad);
    }
}
