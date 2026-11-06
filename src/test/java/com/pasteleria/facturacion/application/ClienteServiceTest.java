package com.pasteleria.facturacion.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pasteleria.facturacion.application.port.in.command.ActualizarClienteCommand;
import com.pasteleria.facturacion.application.port.in.command.RegistrarClienteCommand;
import com.pasteleria.facturacion.application.service.ClienteService;
import com.pasteleria.facturacion.domain.exception.ClienteDuplicadoException;
import com.pasteleria.facturacion.domain.exception.ClienteNoEncontradoException;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.testsupport.ClienteRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClienteServiceTest {

    private ClienteRepositoryEnMemoria repositorio;
    private ClienteService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new ClienteRepositoryEnMemoria();
        servicio = new ClienteService(repositorio);
    }

    private Cliente registrarAna() {
        return servicio.registrar(new RegistrarClienteCommand("1090123456", "Ana Pérez", "3001234567", "ana@correo.com"));
    }

    @Test
    @DisplayName("HU-9: registra nombre, documento, teléfono y correo, y se busca por documento")
    void registraYBuscaPorDocumento() {
        registrarAna();

        Cliente encontrado = servicio.porDocumento("1090123456");

        assertEquals("Ana Pérez", encontrado.getNombre());
        assertEquals("ana@correo.com", encontrado.getCorreo());
    }

    @Test
    @DisplayName("HU-9: no permite dos clientes con el mismo documento")
    void rechazaDocumentoRepetido() {
        registrarAna();

        assertThrows(ClienteDuplicadoException.class,
                () -> servicio.registrar(new RegistrarClienteCommand("1090123456", "Otra Persona", null, null)));
        assertEquals(1, repositorio.cantidad());
    }

    @Test
    void actualizaLosDatos() {
        registrarAna();

        servicio.actualizar(new ActualizarClienteCommand("1090123456", "Ana María", "3109998888", "nueva@correo.com"));

        Cliente actualizado = servicio.porDocumento("1090123456");
        assertEquals("Ana María", actualizado.getNombre());
        assertEquals("nueva@correo.com", actualizado.getCorreo());
    }

    @Test
    void falla_si_el_cliente_no_existe_o_el_dato_es_invalido() {
        assertThrows(ClienteNoEncontradoException.class, () -> servicio.porDocumento("99999999"));
        assertThrows(ClienteNoEncontradoException.class,
                () -> servicio.actualizar(new ActualizarClienteCommand("99999999", "X", null, null)));
        assertThrows(ValorInvalidoException.class,
                () -> servicio.registrar(new RegistrarClienteCommand("12", "Ana", null, null)));
    }
}
