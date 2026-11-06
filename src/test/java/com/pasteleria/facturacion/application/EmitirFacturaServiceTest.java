package com.pasteleria.facturacion.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pasteleria.facturacion.application.port.in.command.EmitirFacturaCommand;
import com.pasteleria.facturacion.application.port.in.command.EmitirFacturaCommand.Linea;
import com.pasteleria.facturacion.application.service.EmitirFacturaService;
import com.pasteleria.facturacion.domain.exception.ClienteNoEncontradoException;
import com.pasteleria.facturacion.domain.exception.ProductoNoEncontradoException;
import com.pasteleria.facturacion.domain.exception.StockInsuficienteException;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.EstadoFactura;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.service.EmisionFacturaService;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.testsupport.ClienteRepositoryEnMemoria;
import com.pasteleria.facturacion.testsupport.Datos;
import com.pasteleria.facturacion.testsupport.FacturaRepositoryEnMemoria;
import com.pasteleria.facturacion.testsupport.NumeracionEnMemoria;
import com.pasteleria.facturacion.testsupport.ProductoRepositoryEnMemoria;
import com.pasteleria.facturacion.testsupport.TransaccionDirecta;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Sin Spring, sin base de datos y con reloj fijo: el caso de uso se prueba solo con puertos (Semana 5, DIP). */
class EmitirFacturaServiceTest {

    private ClienteRepositoryEnMemoria clientes;
    private ProductoRepositoryEnMemoria productos;
    private FacturaRepositoryEnMemoria facturas;
    private EmitirFacturaService servicio;

    @BeforeEach
    void preparar() {
        clientes = new ClienteRepositoryEnMemoria();
        productos = new ProductoRepositoryEnMemoria();
        facturas = new FacturaRepositoryEnMemoria();
        servicio = new EmitirFacturaService(clientes, productos, facturas, new NumeracionEnMemoria(),
                new TransaccionDirecta(), Datos.relojFijo(), new EmisionFacturaService());

        clientes.guardar(Datos.ana());
        productos.guardar(Datos.tortaChocolate(10));
        productos.guardar(Datos.cupcake(20));
    }

    private int stock(String codigo) {
        return productos.buscarPorCodigo(CodigoProducto.de(codigo)).orElseThrow().getStock();
    }

    @Test
    @DisplayName("HU-10: la factura queda guardada con número único, fecha de emisión y total")
    void emiteYGuardaLaFactura() {
        Factura factura = servicio.emitir(new EmitirFacturaCommand("1090123456",
                List.of(new Linea("TOR-001", 2), new Linea("CUP-001", 6))));

        assertEquals("FAC-2026-000001", factura.getNumero().valor());
        assertEquals(LocalDateTime.of(2026, 10, 20, 10, 0), factura.getFechaEmision());
        assertEquals(Dinero.de(129000), factura.total());
        assertEquals(EstadoFactura.EMITIDA, factura.getEstado());
        assertEquals(1, facturas.cantidad());
    }

    @Test
    @DisplayName("HU-11: al confirmar la venta, el stock de cada producto queda descontado y guardado")
    void descuentaYPersisteElStock() {
        servicio.emitir(new EmitirFacturaCommand("1090123456",
                List.of(new Linea("TOR-001", 2), new Linea("CUP-001", 6))));

        assertEquals(8, stock("TOR-001"));
        assertEquals(14, stock("CUP-001"));
    }

    @Test
    void cadaFacturaRecibeUnConsecutivoNuevo() {
        Factura primera = servicio.emitir(new EmitirFacturaCommand("1090123456", List.of(new Linea("TOR-001", 1))));
        Factura segunda = servicio.emitir(new EmitirFacturaCommand("1090123456", List.of(new Linea("TOR-001", 1))));

        assertEquals("FAC-2026-000001", primera.getNumero().valor());
        assertEquals("FAC-2026-000002", segunda.getNumero().valor());
    }

    @Test
    @DisplayName("HU-11: sin stock suficiente la venta se cancela: no hay factura y el stock no cambia")
    void sinStockNoSeGuardaNada() {
        StockInsuficienteException error = assertThrows(StockInsuficienteException.class,
                () -> servicio.emitir(new EmitirFacturaCommand("1090123456",
                        List.of(new Linea("TOR-001", 3), new Linea("CUP-001", 25)))));

        assertEquals(1, error.getFaltantes().size());
        assertEquals("CUP-001", error.getFaltantes().get(0).codigo().valor());
        assertEquals(0, facturas.cantidad());
        assertEquals(10, stock("TOR-001"));
        assertEquals(20, stock("CUP-001"));
    }

    @Test
    void elMismoProductoEnDosLineasSeSumaAntesDeValidar() {
        assertThrows(StockInsuficienteException.class, () -> servicio.emitir(new EmitirFacturaCommand("1090123456",
                List.of(new Linea("TOR-001", 6), new Linea("TOR-001", 6)))));
        assertEquals(10, stock("TOR-001"));

        Factura factura = servicio.emitir(new EmitirFacturaCommand("1090123456",
                List.of(new Linea("TOR-001", 3), new Linea("TOR-001", 4))));

        assertEquals(1, factura.getLineas().size());
        assertEquals(3, stock("TOR-001"));
    }

    @Test
    void clienteInexistenteNoDejaRastro() {
        assertThrows(ClienteNoEncontradoException.class, () -> servicio.emitir(
                new EmitirFacturaCommand("99999999", List.of(new Linea("TOR-001", 1)))));

        assertEquals(0, facturas.cantidad());
        assertEquals(10, stock("TOR-001"));
    }

    @Test
    void productoInexistenteNoDejaRastro() {
        assertThrows(ProductoNoEncontradoException.class, () -> servicio.emitir(new EmitirFacturaCommand(
                "1090123456", List.of(new Linea("TOR-001", 1), new Linea("NOEXISTE", 1)))));

        assertEquals(0, facturas.cantidad());
        assertEquals(10, stock("TOR-001"));
    }

    @Test
    void exigeAlMenosUnaLineaConCantidadPositiva() {
        assertThrows(ValorInvalidoException.class,
                () -> servicio.emitir(new EmitirFacturaCommand("1090123456", List.of())));
        assertThrows(ValorInvalidoException.class,
                () -> servicio.emitir(new EmitirFacturaCommand("1090123456", List.of(new Linea("TOR-001", 0)))));
        assertEquals(0, facturas.cantidad());
    }
}
