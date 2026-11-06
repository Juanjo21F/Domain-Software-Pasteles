package com.pasteleria.facturacion.domain.exception;

import com.pasteleria.facturacion.domain.vo.CodigoProducto;

public class ProductoInactivoException extends DominioException {

    public ProductoInactivoException(CodigoProducto codigo) {
        super("El producto " + codigo.valor() + " está desactivado y no puede venderse");
    }
}
