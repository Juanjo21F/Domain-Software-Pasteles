package com.pasteleria.facturacion.application.port.in;

import com.pasteleria.facturacion.domain.service.ReporteVentasMensual;

public interface ConsultarReporteVentasUseCase {
    ReporteVentasMensual generar(int anio, int mes);
}
