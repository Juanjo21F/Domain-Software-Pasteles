package com.pasteleria.facturacion.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.FacturaBuilder;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.service.ReporteVentasMensual;
import com.pasteleria.facturacion.domain.service.ReporteVentasService;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import com.pasteleria.facturacion.testsupport.Datos;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReporteVentasServiceTest {

    private static final YearMonth OCTUBRE = YearMonth.of(2026, 10);

    private final ReporteVentasService servicio = new ReporteVentasService();

    private Factura factura(int secuencia, LocalDateTime fecha, Object... productoYCantidad) {
        FacturaBuilder builder = FacturaBuilder.nueva().numero(NumeroFactura.de(fecha.getYear(), secuencia))
                .fecha(fecha).cliente(Datos.ana());
        for (int i = 0; i < productoYCantidad.length; i += 2) {
            builder.agregarLinea((Producto) productoYCantidad[i], (Integer) productoYCantidad[i + 1]);
        }
        return builder.build();
    }

    @Test
    @DisplayName("HU-12: suma por producto, de mayor a menor venta, con totales")
    void agrupaYOrdenaPorUnidadesVendidas() {
        Producto torta = Datos.tortaChocolate(100);
        Producto cupcake = Datos.cupcake(100);
        List<Factura> facturas = List.of(
                factura(1, LocalDateTime.of(2026, 10, 3, 9, 0), torta, 2, cupcake, 5),
                factura(2, LocalDateTime.of(2026, 10, 15, 16, 30), cupcake, 4, torta, 1));

        ReporteVentasMensual reporte = servicio.generar(OCTUBRE, facturas);

        assertEquals(2, reporte.lineas().size());
        assertEquals(CodigoProducto.de("CUP-001"), reporte.lineas().get(0).codigo());   // 9 unidades
        assertEquals(9, reporte.lineas().get(0).cantidadVendida());
        assertEquals(Dinero.de(58500), reporte.lineas().get(0).ingresos());             // 9 x 6.500
        assertEquals(CodigoProducto.de("TOR-001"), reporte.lineas().get(1).codigo());   // 3 unidades
        assertEquals(Dinero.de(135000), reporte.lineas().get(1).ingresos());
        assertEquals(12, reporte.totalUnidades());
        assertEquals(Dinero.de(193500), reporte.totalIngresos());
    }

    @Test
    @DisplayName("HU-12: las facturas anuladas no se cuentan")
    void ignoraFacturasAnuladas() {
        Producto torta = Datos.tortaChocolate(100);
        Factura vigente = factura(1, LocalDateTime.of(2026, 10, 3, 9, 0), torta, 2);
        Factura anulada = factura(2, LocalDateTime.of(2026, 10, 4, 9, 0), torta, 10);
        anulada.anular();

        ReporteVentasMensual reporte = servicio.generar(OCTUBRE, List.of(vigente, anulada));

        assertEquals(2, reporte.lineas().get(0).cantidadVendida());
        assertEquals(Dinero.de(90000), reporte.totalIngresos());
    }

    @Test
    void ignoraFacturasDeOtrosMeses() {
        Producto torta = Datos.tortaChocolate(100);
        List<Factura> facturas = List.of(
                factura(1, LocalDateTime.of(2026, 9, 30, 23, 59), torta, 7),
                factura(2, LocalDateTime.of(2026, 10, 1, 0, 0), torta, 1),
                factura(3, LocalDateTime.of(2026, 10, 31, 23, 59), torta, 1),
                factura(4, LocalDateTime.of(2026, 11, 1, 0, 0), torta, 9));

        ReporteVentasMensual reporte = servicio.generar(OCTUBRE, facturas);

        assertEquals(2, reporte.lineas().get(0).cantidadVendida());
    }

    @Test
    void siHayEmpateGanaElQueMasIngresaYLuegoElCodigo() {
        Producto torta = Datos.tortaChocolate(100);   // 45.000
        Producto cupcake = Datos.cupcake(100);        // 6.500
        Producto galleta = Datos.galleta(100);        // 2.500
        List<Factura> facturas = List.of(
                factura(1, LocalDateTime.of(2026, 10, 3, 9, 0), galleta, 2, cupcake, 2, torta, 2));

        ReporteVentasMensual reporte = servicio.generar(OCTUBRE, facturas);

        assertEquals("TOR-001", reporte.lineas().get(0).codigo().valor());
        assertEquals("CUP-001", reporte.lineas().get(1).codigo().valor());
        assertEquals("GAL-001", reporte.lineas().get(2).codigo().valor());
    }

    @Test
    void unMesSinVentasDaUnReporteVacio() {
        ReporteVentasMensual reporte = servicio.generar(OCTUBRE, List.of());

        assertTrue(reporte.lineas().isEmpty());
        assertEquals(0, reporte.totalUnidades());
        assertEquals(Dinero.CERO, reporte.totalIngresos());
    }
}
