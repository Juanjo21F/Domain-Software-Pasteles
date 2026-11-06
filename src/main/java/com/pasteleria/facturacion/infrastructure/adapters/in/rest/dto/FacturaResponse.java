package com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto;

import com.pasteleria.facturacion.domain.model.Factura;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FacturaResponse(String numero, LocalDateTime fechaEmision, String clienteDocumento, String estado,
                              List<LineaResponse> lineas, BigDecimal total) {

    public record LineaResponse(String codigoProducto, String nombreProducto, BigDecimal precioUnitario,
                                int cantidad, BigDecimal subtotal) { }

    public static FacturaResponse de(Factura f) {
        List<LineaResponse> lineas = f.getLineas().stream()
                .map(l -> new LineaResponse(l.codigoProducto().valor(), l.nombreProducto(),
                        l.precioUnitario().valor(), l.cantidad(), l.subtotal().valor()))
                .toList();
        return new FacturaResponse(f.getNumero().valor(), f.getFechaEmision(), f.getClienteDocumento().valor(),
                f.getEstado().name(), lineas, f.total().valor());
    }
}
