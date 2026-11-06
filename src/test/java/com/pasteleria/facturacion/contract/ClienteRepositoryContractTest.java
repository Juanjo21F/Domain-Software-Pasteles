package com.pasteleria.facturacion.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pasteleria.facturacion.application.port.out.ClienteRepositoryPort;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.vo.Documento;
import com.pasteleria.facturacion.testsupport.Datos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class ClienteRepositoryContractTest {

    protected abstract ClienteRepositoryPort crearRepositorio();

    private ClienteRepositoryPort repositorio;

    @BeforeEach
    void iniciar() {
        repositorio = crearRepositorio();
    }

    @Test
    void guardarYBuscarPorDocumento() {
        repositorio.guardar(Datos.ana());

        Cliente leido = repositorio.buscarPorDocumento(Documento.de("1090123456")).orElseThrow();

        assertEquals("Ana Pérez", leido.getNombre());
        assertEquals("3001234567", leido.getTelefono());
        assertEquals("ana@correo.com", leido.getCorreo());
        assertTrue(repositorio.existePorDocumento(Documento.de("1090123456")));
    }

    @Test
    void buscarUnoInexistenteDevuelveVacio() {
        assertFalse(repositorio.buscarPorDocumento(Documento.de("99999999")).isPresent());
        assertFalse(repositorio.existePorDocumento(Documento.de("99999999")));
    }

    @Test
    void guardarDosVecesActualizaYAceptaCamposVacios() {
        repositorio.guardar(Datos.ana());
        Cliente ana = repositorio.buscarPorDocumento(Documento.de("1090123456")).orElseThrow();
        ana.actualizarDatos("Ana María", null, null);
        repositorio.guardar(ana);

        Cliente leido = repositorio.buscarPorDocumento(Documento.de("1090123456")).orElseThrow();

        assertEquals("Ana María", leido.getNombre());
        assertNull(leido.getTelefono());
        assertNull(leido.getCorreo());
    }
}
