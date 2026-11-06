package com.pasteleria.facturacion.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pasteleria.facturacion.application.service.ConsultarReporteVentasService;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.FacturaBuilder;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.service.ReporteVentasMensual;
import com.pasteleria.facturacion.domain.service.ReporteVentasService;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import com.pasteleria.facturacion.testsupport.Datos;
import com.pasteleria.facturacion.testsupport.FacturaRepositoryEnMemoria;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsultarReporteVentasServiceTest {

    private FacturaRepositoryEnMemoria facturas;
    private ConsultarReporteVentasService servicio;
    private int consecutivo;

    @BeforeEach
    void preparar() {
        facturas = new FacturaRepositoryEnMemoria();
        servicio = new ConsultarReporteVentasService(facturas, new ReporteVentasService());
        consecutivo = 0;
    }

    private Factura guardar(LocalDateTime fecha, Producto producto, int cantidad) {
        Factura factura = FacturaBuilder.nueva().numero(NumeroFactura.de(fecha.getYear(), ++consecutivo))
                .fecha(fecha).cliente(Datos.ana()).agregarLinea(producto, cantidad).build();
        return facturas.guardar(factura);
    }

    @Test
    @DisplayName("HU-12: reporte del mes elegido, con las facturas anuladas y de otros meses fuera")
    void reporteDelMes() {
        Producto torta = Datos.tortaChocolate(100);
        Producto cupcake = Datos.cupcake(100);
        guardar(LocalDateTime.of(2026, 10, 2, 10, 0), torta, 2);
        guardar(LocalDateTime.of(2026, 10, 9, 10, 0), cupcake, 5);
        Factura anulada = guardar(LocalDateTime.of(2026, 10, 12, 10, 0), torta, 50);
        anulada.anular();
        facturas.guardar(anulada);
        guardar(LocalDateTime.of(2026, 9, 30, 23, 59), torta, 40);   // septiembre
        guardar(LocalDateTime.of(2026, 11, 1, 0, 0), torta, 40);     // noviembre

        ReporteVentasMensual reporte = servicio.generar(2026, 10);

        assertEquals(2, reporte.lineas().size());
        assertEquals("CUP-001", reporte.lineas().get(0).codigo().valor());
        assertEquals(5, reporte.lineas().get(0).cantidadVendida());
        assertEquals("TOR-001", reporte.lineas().get(1).codigo().valor());
        assertEquals(2, reporte.lineas().get(1).cantidadVendida());
        assertEquals(Dinero.de(122500), reporte.totalIngresos());
    }

    @Test
    void validaElPeriodoPedido() {
        assertThrows(ValorInvalidoException.class, () -> servicio.generar(2026, 13));
        assertThrows(ValorInvalidoException.class, () -> servicio.generar(2026, 0));
        assertThrows(ValorInvalidoException.class, () -> servicio.generar(1850, 5));
    }
}
