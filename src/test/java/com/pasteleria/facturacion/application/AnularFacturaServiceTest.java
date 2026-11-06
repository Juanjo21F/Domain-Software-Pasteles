package com.pasteleria.facturacion.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pasteleria.facturacion.application.port.in.command.EmitirFacturaCommand;
import com.pasteleria.facturacion.application.port.in.command.EmitirFacturaCommand.Linea;
import com.pasteleria.facturacion.application.service.AnularFacturaService;
import com.pasteleria.facturacion.application.service.EmitirFacturaService;
import com.pasteleria.facturacion.domain.exception.FacturaNoEncontradaException;
import com.pasteleria.facturacion.domain.exception.FacturaYaAnuladaException;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.service.EmisionFacturaService;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.testsupport.ClienteRepositoryEnMemoria;
import com.pasteleria.facturacion.testsupport.Datos;
import com.pasteleria.facturacion.testsupport.FacturaRepositoryEnMemoria;
import com.pasteleria.facturacion.testsupport.NumeracionEnMemoria;
import com.pasteleria.facturacion.testsupport.ProductoRepositoryEnMemoria;
import com.pasteleria.facturacion.testsupport.TransaccionDirecta;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnularFacturaServiceTest {

    private ProductoRepositoryEnMemoria productos;
    private FacturaRepositoryEnMemoria facturas;
    private EmitirFacturaService emitir;
    private AnularFacturaService anular;

    @BeforeEach
    void preparar() {
        ClienteRepositoryEnMemoria clientes = new ClienteRepositoryEnMemoria();
        productos = new ProductoRepositoryEnMemoria();
        facturas = new FacturaRepositoryEnMemoria();
        TransaccionDirecta transaccion = new TransaccionDirecta();
        emitir = new EmitirFacturaService(clientes, productos, facturas, new NumeracionEnMemoria(),
                transaccion, Datos.relojFijo(), new EmisionFacturaService());
        anular = new AnularFacturaService(facturas, productos, transaccion);

        clientes.guardar(Datos.ana());
        productos.guardar(Datos.tortaChocolate(10));
    }

    private int stock() {
        return productos.buscarPorCodigo(CodigoProducto.de("TOR-001")).orElseThrow().getStock();
    }

    private Factura emitirDosTortas() {
        return emitir.emitir(new EmitirFacturaCommand("1090123456", List.of(new Linea("TOR-001", 2))));
    }

    @Test
    @DisplayName("Anular una factura la marca como anulada y devuelve el stock")
    void anulaYRestituyeElStock() {
        Factura factura = emitirDosTortas();
        assertEquals(8, stock());

        Factura anulada = anular.anular(factura.getNumero().valor());

        assertTrue(anulada.estaAnulada());
        assertEquals(10, stock());
        assertTrue(facturas.buscarPorNumero(factura.getNumero()).orElseThrow().estaAnulada());
    }

    @Test
    @DisplayName("Anular dos veces falla y NO repone el stock dos veces")
    void noSePuedeAnularDosVeces() {
        Factura factura = emitirDosTortas();
        anular.anular(factura.getNumero().valor());

        assertThrows(FacturaYaAnuladaException.class, () -> anular.anular(factura.getNumero().valor()));
        assertEquals(10, stock());
    }

    @Test
    void facturaInexistente() {
        assertThrows(FacturaNoEncontradaException.class, () -> anular.anular("FAC-2026-000999"));
    }
}
