package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.application.port.in.command.RegistrarProductoCommand;
import com.pasteleria.facturacion.domain.model.Producto;

public interface RegistrarProductoUseCase {
    Producto registrar(RegistrarProductoCommand comando);
}
