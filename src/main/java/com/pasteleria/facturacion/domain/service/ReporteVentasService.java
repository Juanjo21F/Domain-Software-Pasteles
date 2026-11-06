package com.pasteleria.facturacion.domain.service;

import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de dominio para HU-12. La regla "las facturas anuladas no cuentan" vive aquí, en el dominio,
 * y no en la consulta SQL del adaptador: así se prueba sin base de datos.
 */
public class ReporteVentasService {

    private record Acumulado(String nombre, long cantidad, Dinero ingresos) { }

    public ReporteVentasMensual generar(YearMonth periodo, List<Factura> facturas) {
        Map<CodigoProducto, Acumulado> acumulados = new LinkedHashMap<>();

        facturas.stream()
                .filter(f -> !f.estaAnulada())
                .filter(f -> YearMonth.from(f.getFechaEmision()).equals(periodo))
                .sorted(Comparator.comparing(Factura::getFechaEmision))
                .forEach(f -> f.getLineas().forEach(linea -> acumulados.merge(
                        linea.codigoProducto(),
                        new Acumulado(linea.nombreProducto(), linea.cantidad(), linea.subtotal()),
                        // si el nombre cambió durante el mes, queda el más reciente
                        (a, b) -> new Acumulado(b.nombre(), a.cantidad() + b.cantidad(), a.ingresos().sumar(b.ingresos())))));

        // De mayor a menor venta; si empatan, más ingresos primero; si aún empatan, por código (orden estable).
        Comparator<LineaReporte> orden = Comparator
                .comparingLong(LineaReporte::cantidadVendida).reversed()
                .thenComparing(Comparator.comparing(LineaReporte::ingresos).reversed())
                .thenComparing(l -> l.codigo().valor());

        List<LineaReporte> lineas = acumulados.entrySet().stream()
                .map(e -> new LineaReporte(e.getKey(), e.getValue().nombre(), e.getValue().cantidad(), e.getValue().ingresos()))
                .sorted(orden)
                .toList();

        long totalUnidades = lineas.stream().mapToLong(LineaReporte::cantidadVendida).sum();
        Dinero totalIngresos = lineas.stream().map(LineaReporte::ingresos).reduce(Dinero.CERO, Dinero::sumar);
        return new ReporteVentasMensual(periodo, lineas, totalUnidades, totalIngresos);
    }
}
