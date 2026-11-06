package com.pasteleria.facturacion.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pasteleria.facturacion.application.port.in.command.ModificarProductoCommand;
import com.pasteleria.facturacion.application.port.in.command.RegistrarProductoCommand;
import com.pasteleria.facturacion.application.service.ProductoService;
import com.pasteleria.facturacion.domain.exception.ProductoDuplicadoException;
import com.pasteleria.facturacion.domain.exception.ProductoNoEncontradoException;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.testsupport.ProductoRepositoryEnMemoria;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductoServiceTest {

    private ProductoRepositoryEnMemoria repositorio;
    private ProductoService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new ProductoRepositoryEnMemoria();
        servicio = new ProductoService(repositorio);
    }

    private Producto registrarTorta() {
        return servicio.registrar(new RegistrarProductoCommand(
                "tor-001", "Torta de chocolate", "Tortas", new BigDecimal("45000"), 10));
    }

    @Test
    @DisplayName("HU-8: crea un producto con código, nombre, categoría, precio y stock")
    void registraUnProducto() {
        Producto torta = registrarTorta();

        assertEquals("TOR-001", torta.getCodigo().valor());
        assertEquals(Dinero.de(45000), torta.getPrecio());
        assertTrue(repositorio.existePorCodigo(torta.getCodigo()));
    }

    @Test
    @DisplayName("HU-8: no permite repetir el código de un producto")
    void rechazaCodigoRepetido() {
        registrarTorta();

        assertThrows(ProductoDuplicadoException.class, this::registrarTorta);
        assertEquals(1, repositorio.listar(false).size());
    }

    @Test
    void rechazaDatosInvalidos() {
        assertThrows(ValorInvalidoException.class, () -> servicio.registrar(new RegistrarProductoCommand(
                "TOR-002", "Torta", "Tortas", null, 1)));
        assertThrows(ValorInvalidoException.class, () -> servicio.registrar(new RegistrarProductoCommand(
                "TOR-002", "Torta", "Tortas", new BigDecimal("-5"), 1)));
        assertTrue(repositorio.listar(false).isEmpty());
    }

    @Test
    void modificaYPersisteLosCambios() {
        registrarTorta();

        servicio.modificar(new ModificarProductoCommand("TOR-001", "Torta grande", "Especiales", new BigDecimal("60000")));

        Producto guardado = servicio.porCodigo("TOR-001");
        assertEquals("Torta grande", guardado.getNombre());
        assertEquals(Dinero.de(60000), guardado.getPrecio());
        assertEquals(10, guardado.getStock());
    }

    @Test
    @DisplayName("HU-8: puede desactivar un producto y deja de aparecer entre los activos")
    void desactivaYPersiste() {
        registrarTorta();

        servicio.desactivar("TOR-001");

        assertFalse(servicio.porCodigo("TOR-001").isActivo());
        assertTrue(servicio.listar(true).isEmpty());
        assertEquals(1, servicio.listar(false).size());
    }

    @Test
    void consultarUnoInexistenteFalla() {
        assertThrows(ProductoNoEncontradoException.class, () -> servicio.porCodigo("NOEXISTE"));
        assertThrows(ProductoNoEncontradoException.class, () -> servicio.desactivar("NOEXISTE"));
    }
}
