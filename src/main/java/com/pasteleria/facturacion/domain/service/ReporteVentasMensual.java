package com.pasteleria.facturacion.domain.service;

import com.pasteleria.facturacion.domain.vo.Dinero;
import java.time.YearMonth;
import java.util.List;

public record ReporteVentasMensual(YearMonth periodo, List<LineaReporte> lineas, long totalUnidades, Dinero totalIngresos) {

    public ReporteVentasMensual {
        lineas = List.copyOf(lineas);
    }
}
