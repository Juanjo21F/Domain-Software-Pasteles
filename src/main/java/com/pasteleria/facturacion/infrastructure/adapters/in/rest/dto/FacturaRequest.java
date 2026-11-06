package com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto;

import java.util.List;

public record FacturaRequest(String clienteDocumento, List<LineaRequest> lineas) {

    public record LineaRequest(String codigoProducto, int cantidad) { }
}
