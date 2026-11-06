package com.pasteleria.facturacion.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pasteleria.facturacion.domain.exception.ProductoInactivoException;
import com.pasteleria.facturacion.domain.exception.StockInsuficienteException;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.testsupport.Datos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductoTest {

    @Test
    void unProductoNuevoNaceActivo() {
        Producto torta = Datos.tortaChocolate(10);

        assertTrue(torta.isActivo());
        assertEquals(10, torta.getStock());
    }

    @Test
    @DisplayName("El código se normaliza: ' tor-001 ' y 'TOR-001' son el mismo producto")
    void normalizaElCodigo() {
        assertEquals(CodigoProducto.de("TOR-001"), CodigoProducto.de(" tor-001 "));
    }

    @Test
    void rechazaDatosInvalidos() {
        CodigoProducto codigo = CodigoProducto.de("TOR-001");
        assertThrows(ValorInvalidoException.class, () -> Producto.crear(codigo, " ", "Tortas", Dinero.de(100), 1));
        assertThrows(ValorInvalidoException.class, () -> Producto.crear(codigo, "Torta", "", Dinero.de(100), 1));
        assertThrows(ValorInvalidoException.class, () -> Producto.crear(codigo, "Torta", "Tortas", Dinero.CERO, 1));
        assertThrows(ValorInvalidoException.class, () -> Producto.crear(codigo, "Torta", "Tortas", Dinero.de(100), -1));
        assertThrows(ValorInvalidoException.class, () -> CodigoProducto.de("a"));
        assertThrows(ValorInvalidoException.class, () -> CodigoProducto.de("TOR 001"));
    }

    @Test
    void descontarStockReduceLasExistencias() {
        Producto torta = Datos.tortaChocolate(10);

        torta.descontarStock(4);

        assertEquals(6, torta.getStock());
    }

    @Test
    @DisplayName("HU-11: nunca deja el stock negativo")
    void noPermiteVenderMasDeLoDisponible() {
        Producto torta = Datos.tortaChocolate(3);

        StockInsuficienteException error = assertThrows(StockInsuficienteException.class, () -> torta.descontarStock(4));

        assertEquals(3, torta.getStock());
        assertEquals(1, error.getFaltantes().size());
        assertEquals(3, error.getFaltantes().get(0).disponible());
        assertEquals(4, error.getFaltantes().get(0).solicitado());
    }

    @Test
    void unProductoDesactivadoNoSeVende() {
        Producto torta = Datos.tortaChocolate(10);
        torta.desactivar();

        assertFalse(torta.isActivo());
        assertFalse(torta.puedeVender(1));
        assertThrows(ProductoInactivoException.class, () -> torta.descontarStock(1));
    }

    @Test
    void desactivarEsIdempotente() {
        Producto torta = Datos.tortaChocolate(10);
        torta.desactivar();
        torta.desactivar();

        assertFalse(torta.isActivo());
    }

    @Test
    void modificarCambiaLosDatosPeroNoElCodigoNiElStock() {
        Producto torta = Datos.tortaChocolate(10);

        torta.modificar("Torta de chocolate grande", "Tortas especiales", Dinero.de(60000));

        assertEquals("Torta de chocolate grande", torta.getNombre());
        assertEquals(Dinero.de(60000), torta.getPrecio());
        assertEquals(CodigoProducto.de("TOR-001"), torta.getCodigo());
        assertEquals(10, torta.getStock());
        assertThrows(ValorInvalidoException.class, () -> torta.modificar("X", "Y", Dinero.CERO));
    }

    @Test
    void reponerStockSumaExistencias() {
        Producto torta = Datos.tortaChocolate(2);

        torta.reponerStock(5);

        assertEquals(7, torta.getStock());
        assertThrows(ValorInvalidoException.class, () -> torta.reponerStock(0));
    }
}
