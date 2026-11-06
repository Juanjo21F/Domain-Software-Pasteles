package com.pasteleria.facturacion.infrastructure.adapters.in.rest;

import com.pasteleria.facturacion.application.port.in.ConsultarReporteVentasUseCase;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.ReporteVentasResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** HU-12: reporte mensual de productos más vendidos. */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ConsultarReporteVentasUseCase reporte;

    public ReporteController(ConsultarReporteVentasUseCase reporte) {
        this.reporte = reporte;
    }

    @GetMapping("/ventas-mensuales")
    public ReporteVentasResponse ventasMensuales(@RequestParam int anio, @RequestParam int mes) {
        return ReporteVentasResponse.de(reporte.generar(anio, mes));
    }
}
