package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.application.port.in.command.EmitirFacturaCommand;
import com.pasteleria.facturacion.domain.model.Factura;

public interface EmitirFacturaUseCase {
    Factura emitir(EmitirFacturaCommand comando);
}
