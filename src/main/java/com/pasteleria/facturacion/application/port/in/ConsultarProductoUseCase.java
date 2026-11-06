package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.domain.model.Producto;
import java.util.List;

public interface ConsultarProductoUseCase {
    Producto porCodigo(String codigo);

    List<Producto> listar(boolean soloActivos);
}
