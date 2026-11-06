package com.pasteleria.facturacion.domain.exception;

import com.pasteleria.facturacion.domain.vo.CodigoProducto;

public class ProductoDuplicadoException extends DuplicadoException {

    public ProductoDuplicadoException(CodigoProducto codigo) {
        super("Ya existe un producto con código " + codigo.valor());
    }
}
