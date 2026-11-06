package com.pasteleria.facturacion.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.FacturaBuilder;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import com.pasteleria.facturacion.testsupport.Datos;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public abstract class FacturaRepositoryContractTest {

    protected abstract FacturaRepositoryPort crearRepositorio();

    private FacturaRepositoryPort repositorio;

    @BeforeEach
    void iniciar() {
        repositorio = crearRepositorio();
    }

    private Factura nueva(int secuencia, LocalDateTime fecha) {
        return FacturaBuilder.nueva().numero(NumeroFactura.de(fecha.getYear(), secuencia)).fecha(fecha)
                .cliente(Datos.ana())
                .agregarLinea(Datos.tortaChocolate(10), 2)
                .agregarLinea(Datos.cupcake(10), 3)
                .build();
    }

    @Test
    @DisplayName("Se guardan y se leen las líneas, en orden, con el precio del momento")
    void guardarYBuscarConLineas() {
        repositorio.guardar(nueva(1, LocalDateTime.of(2026, 10, 20, 10, 0)));

        Factura leida = repositorio.buscarPorNumero(NumeroFactura.de(2026, 1)).orElseThrow();

        assertEquals(2, leida.getLineas().size());
        assertEquals("TOR-001", leida.getLineas().get(0).codigoProducto().valor());
        assertEquals("CUP-001", leida.getLineas().get(1).codigoProducto().valor());
        assertEquals(Dinero.de(45000), leida.getLineas().get(0).precioUnitario());
        assertEquals(3, leida.getLineas().get(1).cantidad());
        assertEquals(Dinero.de(109500), leida.total());
        assertEquals("1090123456", leida.getClienteDocumento().valor());
        assertEquals(LocalDateTime.of(2026, 10, 20, 10, 0), leida.getFechaEmision());
    }

    @Test
    void buscarUnaInexistenteDevuelveVacio() {
        assertFalse(repositorio.buscarPorNumero(NumeroFactura.de(2026, 99)).isPresent());
    }

    @Test
    @DisplayName("Anular y guardar de nuevo conserva las líneas y cambia solo el estado")
    void guardarUnaFacturaAnulada() {
        repositorio.guardar(nueva(1, LocalDateTime.of(2026, 10, 20, 10, 0)));
        Factura factura = repositorio.buscarPorNumero(NumeroFactura.de(2026, 1)).orElseThrow();
        factura.anular();
        repositorio.guardar(factura);

        Factura leida = repositorio.buscarPorNumero(NumeroFactura.de(2026, 1)).orElseThrow();

        assertTrue(leida.estaAnulada());
        assertEquals(2, leida.getLineas().size());
    }

    @Test
    @DisplayName("El rango incluye el inicio y excluye el final, y devuelve también las anuladas")
    void buscarPorRangoDeFechas() {
        repositorio.guardar(nueva(1, LocalDateTime.of(2026, 9, 30, 23, 59)));
        repositorio.guardar(nueva(2, LocalDateTime.of(2026, 10, 1, 0, 0)));
        Factura anulada = nueva(3, LocalDateTime.of(2026, 10, 31, 23, 59));
        anulada.anular();
        repositorio.guardar(anulada);
        repositorio.guardar(nueva(4, LocalDateTime.of(2026, 11, 1, 0, 0)));

        List<Factura> octubre = repositorio.buscarEmitidasEntre(
                LocalDateTime.of(2026, 10, 1, 0, 0), LocalDateTime.of(2026, 11, 1, 0, 0));

        assertEquals(2, octubre.size());
        assertEquals("FAC-2026-000002", octubre.get(0).getNumero().valor());
        assertEquals("FAC-2026-000003", octubre.get(1).getNumero().valor());
        assertTrue(octubre.get(1).estaAnulada());
    }
}
