package com.pasteleria.facturacion.domain.exception;

import com.pasteleria.facturacion.domain.vo.NumeroFactura;

public class FacturaNoEncontradaException extends NoEncontradoException {

    public FacturaNoEncontradaException(NumeroFactura numero) {
        super("No existe la factura " + numero.valor());
    }
}
