package com.pasteleria.facturacion.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.testsupport.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Prueba de contrato (LSP): TODA implementación de ProductoRepositoryPort debe pasar exactamente estas pruebas.
 * Hay una subclase para el doble en memoria y otra para el adaptador JPA.
 */
public abstract class ProductoRepositoryContractTest {

    protected abstract ProductoRepositoryPort crearRepositorio();

    private ProductoRepositoryPort repositorio;

    @BeforeEach
    void iniciar() {
        repositorio = crearRepositorio();
    }

    @Test
    @DisplayName("Lo que se guarda es lo que se lee")
    void guardarYBuscar() {
        repositorio.guardar(Datos.tortaChocolate(10));

        Producto leido = repositorio.buscarPorCodigo(CodigoProducto.de("TOR-001")).orElseThrow();

        assertEquals("Torta de chocolate", leido.getNombre());
        assertEquals("Tortas", leido.getCategoria());
        assertEquals(Dinero.de(45000), leido.getPrecio());
        assertEquals(10, leido.getStock());
        assertTrue(leido.isActivo());
    }

    @Test
    void buscarUnoInexistenteDevuelveVacio() {
        assertFalse(repositorio.buscarPorCodigo(CodigoProducto.de("NOEXISTE")).isPresent());
        assertFalse(repositorio.existePorCodigo(CodigoProducto.de("NOEXISTE")));
    }

    @Test
    @DisplayName("Guardar dos veces el mismo código actualiza, no duplica")
    void guardarDosVecesActualiza() {
        repositorio.guardar(Datos.tortaChocolate(10));
        Producto torta = repositorio.buscarPorCodigo(CodigoProducto.de("TOR-001")).orElseThrow();
        torta.descontarStock(4);
        torta.desactivar();
        repositorio.guardar(torta);

        Producto leido = repositorio.buscarPorCodigo(CodigoProducto.de("TOR-001")).orElseThrow();

        assertEquals(1, repositorio.listar(false).size());
        assertEquals(6, leido.getStock());
        assertFalse(leido.isActivo());
    }

    @Test
    void listarPuedeFiltrarSoloLosActivos() {
        repositorio.guardar(Datos.tortaChocolate(10));
        Producto cupcake = Datos.cupcake(5);
        cupcake.desactivar();
        repositorio.guardar(cupcake);

        assertEquals(2, repositorio.listar(false).size());
        assertEquals(1, repositorio.listar(true).size());
        assertEquals("TOR-001", repositorio.listar(true).get(0).getCodigo().valor());
    }
}
