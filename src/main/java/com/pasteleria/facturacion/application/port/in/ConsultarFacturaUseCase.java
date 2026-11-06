package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.domain.model.Factura;

public interface ConsultarFacturaUseCase {
    Factura porNumero(String numeroFactura);
}
