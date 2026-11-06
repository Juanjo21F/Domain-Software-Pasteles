package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.application.port.in.command.RegistrarClienteCommand;
import com.pasteleria.facturacion.domain.model.Cliente;

public interface RegistrarClienteUseCase {
    Cliente registrar(RegistrarClienteCommand comando);
}
