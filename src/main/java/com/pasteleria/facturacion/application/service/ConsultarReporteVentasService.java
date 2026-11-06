package com.pasteleria.facturacion.application.service;

import com.pasteleria.facturacion.application.port.in.ConsultarReporteVentasUseCase;
import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.service.ReporteVentasMensual;
import com.pasteleria.facturacion.domain.service.ReporteVentasService;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/** HU-12: el caso de uso trae las facturas del mes y el servicio de dominio arma el reporte. */
public class ConsultarReporteVentasService implements ConsultarReporteVentasUseCase {

    private final FacturaRepositoryPort facturas;
    private final ReporteVentasService reporte;

    public ConsultarReporteVentasService(FacturaRepositoryPort facturas, ReporteVentasService reporte) {
        this.facturas = facturas;
        this.reporte = reporte;
    }

    @Override
    public ReporteVentasMensual generar(int anio, int mes) {
        YearMonth periodo = periodo(anio, mes);
        LocalDateTime desde = periodo.atDay(1).atStartOfDay();
        LocalDateTime hasta = periodo.plusMonths(1).atDay(1).atStartOfDay();
        List<Factura> delMes = facturas.buscarEmitidasEntre(desde, hasta);
        return reporte.generar(periodo, delMes);
    }

    private static YearMonth periodo(int anio, int mes) {
        if (anio < 2000 || anio > 2100) {
            throw new ValorInvalidoException("El año debe estar entre 2000 y 2100");
        }
        try {
            return YearMonth.of(anio, mes);
        } catch (DateTimeException e) {
            throw new ValorInvalidoException("El mes debe estar entre 1 y 12");
        }
    }
}
