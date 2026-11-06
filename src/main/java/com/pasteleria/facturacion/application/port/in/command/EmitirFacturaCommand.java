package com.pasteleria.facturacion.application.port.in.command;

import java.util.List;

public record EmitirFacturaCommand(String clienteDocumento, List<Linea> lineas) {

    public record Linea(String codigoProducto, int cantidad) { }
}
