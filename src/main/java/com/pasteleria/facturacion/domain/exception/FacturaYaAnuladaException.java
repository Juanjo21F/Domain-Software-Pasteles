package com.pasteleria.facturacion.domain.exception;

import com.pasteleria.facturacion.domain.vo.NumeroFactura;

public class FacturaYaAnuladaException extends DominioException {

    public FacturaYaAnuladaException(NumeroFactura numero) {
        super("La factura " + numero.valor() + " ya está anulada");
    }
}
