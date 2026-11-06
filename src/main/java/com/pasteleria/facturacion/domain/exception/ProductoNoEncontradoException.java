package com.pasteleria.facturacion.domain.exception;

import com.pasteleria.facturacion.domain.vo.CodigoProducto;

public class ProductoNoEncontradoException extends NoEncontradoException {

    public ProductoNoEncontradoException(CodigoProducto codigo) {
        super("No existe el producto " + codigo.valor());
    }
}
