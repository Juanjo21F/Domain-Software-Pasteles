package com.pasteleria.facturacion.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pasteleria.facturacion.domain.exception.FacturaYaAnuladaException;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.EstadoFactura;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.FacturaBuilder;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import com.pasteleria.facturacion.testsupport.Datos;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FacturaBuilderTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 10, 20, 10, 0);
    private static final NumeroFactura NUMERO = NumeroFactura.de(2026, 1);

    private FacturaBuilder base() {
        return FacturaBuilder.nueva().numero(NUMERO).fecha(FECHA).cliente(Datos.ana());
    }

    @Test
    @DisplayName("HU-10: subtotales y total se calculan solos")
    void calculaSubtotalesYTotal() {
        Factura factura = base()
                .agregarLinea(Datos.tortaChocolate(10), 2)   // 2 x 45.000
                .agregarLinea(Datos.cupcake(10), 3)          // 3 x 6.500
                .build();

        assertEquals(2, factura.getLineas().size());
        assertEquals(Dinero.de(90000), factura.getLineas().get(0).subtotal());
        assertEquals(Dinero.de(19500), factura.getLineas().get(1).subtotal());
        assertEquals(Dinero.de(109500), factura.total());
        assertEquals(EstadoFactura.EMITIDA, factura.getEstado());
    }

    @Test
    void elMismoProductoDosVecesSeFusionaEnUnaLinea() {
        Producto torta = Datos.tortaChocolate(10);

        Factura factura = base().agregarLinea(torta, 2).agregarLinea(torta, 1).build();

        assertEquals(1, factura.getLineas().size());
        assertEquals(3, factura.getLineas().get(0).cantidad());
        assertEquals(Dinero.de(135000), factura.total());
    }

    @Test
    @DisplayName("La línea guarda el precio del momento: cambiarlo después no altera la factura emitida")
    void conservaElPrecioDelMomentoDeLaVenta() {
        Producto torta = Datos.tortaChocolate(10);
        Factura factura = base().agregarLinea(torta, 1).build();

        torta.modificar("Torta de chocolate", "Tortas", Dinero.de(99999));

        assertEquals(Dinero.de(45000), factura.total());
    }

    @Test
    void noSeConstruyeIncompleta() {
        assertThrows(ValorInvalidoException.class, () -> base().build());                        // sin líneas
        assertThrows(ValorInvalidoException.class,
                () -> FacturaBuilder.nueva().fecha(FECHA).cliente(Datos.ana())
                        .agregarLinea(Datos.cupcake(5), 1).build());                            // sin número
        assertThrows(ValorInvalidoException.class,
                () -> FacturaBuilder.nueva().numero(NUMERO).fecha(FECHA)
                        .agregarLinea(Datos.cupcake(5), 1).build());                            // sin cliente
        assertThrows(ValorInvalidoException.class, () -> base().agregarLinea(Datos.cupcake(5), 0));
    }

    @Test
    void anularCambiaElEstadoYNoSePuedeAnularDosVeces() {
        Factura factura = base().agregarLinea(Datos.cupcake(5), 1).build();

        factura.anular();

        assertTrue(factura.estaAnulada());
        assertThrows(FacturaYaAnuladaException.class, factura::anular);
    }

    @Test
    void laListaDeLineasNoSePuedeModificarDesdeAfuera() {
        Factura factura = base().agregarLinea(Datos.cupcake(5), 1).build();

        assertThrows(UnsupportedOperationException.class, () -> factura.getLineas().clear());
        assertFalse(factura.getLineas().isEmpty());
    }

    @Test
    void numeroDeFacturaTieneFormatoYConsecutivo() {
        assertEquals("FAC-2026-000007", NumeroFactura.de(2026, 7).valor());
        assertEquals(NumeroFactura.de(2026, 7), NumeroFactura.desde("fac-2026-000007"));
        assertThrows(ValorInvalidoException.class, () -> NumeroFactura.desde("123"));
        assertThrows(ValorInvalidoException.class, () -> NumeroFactura.de(2026, 0));
    }
}
