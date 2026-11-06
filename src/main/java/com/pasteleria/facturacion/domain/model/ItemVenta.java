package com.pasteleria.facturacion.domain.model;

import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;

/** Lo que el empleado quiere vender: un producto y cuántas unidades. */
public record ItemVenta(Producto producto, int cantidad) {

    public ItemVenta {
        if (producto == null) {
            throw new ValorInvalidoException("El producto es obligatorio");
        }
        if (cantidad <= 0) {
            throw new ValorInvalidoException("La cantidad debe ser mayor que cero");
        }
    }
}
