package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.domain.model.Producto;

public interface DesactivarProductoUseCase {
    Producto desactivar(String codigo);
}
