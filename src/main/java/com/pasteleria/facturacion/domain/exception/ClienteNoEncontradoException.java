package com.pasteleria.facturacion.domain.exception;

import com.pasteleria.facturacion.domain.vo.Documento;

public class ClienteNoEncontradoException extends NoEncontradoException {

    public ClienteNoEncontradoException(Documento documento) {
        super("No existe un cliente con documento " + documento.valor());
    }
}
