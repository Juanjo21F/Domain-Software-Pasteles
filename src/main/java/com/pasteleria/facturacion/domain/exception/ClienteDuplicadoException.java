package com.pasteleria.facturacion.domain.exception;

import com.pasteleria.facturacion.domain.vo.Documento;

public class ClienteDuplicadoException extends DuplicadoException {

    public ClienteDuplicadoException(Documento documento) {
        super("Ya existe un cliente con documento " + documento.valor());
    }
}
