package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.domain.model.Cliente;

public interface ConsultarClienteUseCase {
    Cliente porDocumento(String documento);
}
