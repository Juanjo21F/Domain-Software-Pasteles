package com.pasteleria.facturacion.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pasteleria.facturacion.domain.exception.ProductoInactivoException;
import com.pasteleria.facturacion.domain.exception.StockInsuficienteException;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.ItemVenta;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.service.EmisionFacturaService;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import com.pasteleria.facturacion.testsupport.Datos;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmisionFacturaServiceTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 10, 20, 10, 0);
    private static final NumeroFactura NUMERO = NumeroFactura.de(2026, 1);

    private final EmisionFacturaService servicio = new EmisionFacturaService();

    @Test
    @DisplayName("HU-10 y HU-11: emite la factura y descuenta el stock de cada producto")
    void emiteYDescuentaStock() {
        Producto torta = Datos.tortaChocolate(10);
        Producto cupcake = Datos.cupcake(20);

        Factura factura = servicio.emitir(Datos.ana(),
                List.of(new ItemVenta(torta, 2), new ItemVenta(cupcake, 6)), NUMERO, FECHA);

        assertEquals(Dinero.de(129000), factura.total());   // 90.000 + 39.000
        assertEquals(8, torta.getStock());
        assertEquals(14, cupcake.getStock());
    }

    @Test
    @DisplayName("HU-11: si falta stock, se informan TODOS los faltantes y NO se descuenta nada")
    void sinStockSuficienteNoSeTocaNada() {
        Producto torta = Datos.tortaChocolate(10);   // alcanza
        Producto cupcake = Datos.cupcake(2);         // no alcanza
        Producto galleta = Datos.galleta(1);         // no alcanza

        StockInsuficienteException error = assertThrows(StockInsuficienteException.class,
                () -> servicio.emitir(Datos.ana(),
                        List.of(new ItemVenta(torta, 3), new ItemVenta(cupcake, 5), new ItemVenta(galleta, 4)),
                        NUMERO, FECHA));

        assertEquals(2, error.getFaltantes().size());
        assertEquals(10, torta.getStock());      // la torta sí alcanzaba, pero tampoco se descontó
        assertEquals(2, cupcake.getStock());
        assertEquals(1, galleta.getStock());
    }

    @Test
    @DisplayName("Un producto repetido en dos líneas se suma antes de validar el stock")
    void sumaLasLineasRepetidasAntesDeValidar() {
        Producto torta = Datos.tortaChocolate(5);

        // 3 + 3 = 6 > 5: cada línea por separado pasaría, juntas no.
        assertThrows(StockInsuficienteException.class, () -> servicio.emitir(Datos.ana(),
                List.of(new ItemVenta(torta, 3), new ItemVenta(torta, 3)), NUMERO, FECHA));
        assertEquals(5, torta.getStock());

        Factura factura = servicio.emitir(Datos.ana(),
                List.of(new ItemVenta(torta, 2), new ItemVenta(torta, 2)), NUMERO, FECHA);

        assertEquals(1, factura.getLineas().size());
        assertEquals(4, factura.getLineas().get(0).cantidad());
        assertEquals(1, torta.getStock());
    }

    @Test
    void noVendeProductosDesactivados() {
        Producto torta = Datos.tortaChocolate(10);
        torta.desactivar();

        assertThrows(ProductoInactivoException.class,
                () -> servicio.emitir(Datos.ana(), List.of(new ItemVenta(torta, 1)), NUMERO, FECHA));
        assertEquals(10, torta.getStock());
    }

    @Test
    void exigeAlMenosUnProducto() {
        assertThrows(ValorInvalidoException.class, () -> servicio.emitir(Datos.ana(), List.of(), NUMERO, FECHA));
        assertThrows(ValorInvalidoException.class, () -> new ItemVenta(Datos.tortaChocolate(1), 0));
    }
}
