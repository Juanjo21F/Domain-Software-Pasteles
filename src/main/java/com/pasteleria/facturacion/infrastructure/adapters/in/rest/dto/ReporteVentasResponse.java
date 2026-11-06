package com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto;

import com.pasteleria.facturacion.domain.service.ReporteVentasMensual;
import java.math.BigDecimal;
import java.util.List;

public record ReporteVentasResponse(String periodo, List<Fila> productos, long totalUnidades, BigDecimal totalIngresos) {

    public record Fila(String codigo, String nombre, long cantidadVendida, BigDecimal ingresos) { }

    public static ReporteVentasResponse de(ReporteVentasMensual r) {
        List<Fila> filas = r.lineas().stream()
                .map(l -> new Fila(l.codigo().valor(), l.nombre(), l.cantidadVendida(), l.ingresos().valor()))
                .toList();
        return new ReporteVentasResponse(r.periodo().toString(), filas, r.totalUnidades(), r.totalIngresos().valor());
    }
}
