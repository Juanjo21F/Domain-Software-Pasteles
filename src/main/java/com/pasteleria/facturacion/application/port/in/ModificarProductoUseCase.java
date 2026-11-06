package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.application.port.in.command.ModificarProductoCommand;
import com.pasteleria.facturacion.domain.model.Producto;

public interface ModificarProductoUseCase {
    Producto modificar(ModificarProductoCommand comando);
}
