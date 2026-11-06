package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.application.port.in.command.ActualizarClienteCommand;
import com.pasteleria.facturacion.domain.model.Cliente;

public interface ActualizarClienteUseCase {
    Cliente actualizar(ActualizarClienteCommand comando);
}
