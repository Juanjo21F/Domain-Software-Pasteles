package com.pasteleria.facturacion.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.vo.Documento;
import com.pasteleria.facturacion.testsupport.Datos;
import org.junit.jupiter.api.Test;

class ClienteTest {

    @Test
    void registraConTodosSusDatos() {
        Cliente ana = Datos.ana();

        assertEquals("Ana Pérez", ana.getNombre());
        assertEquals("3001234567", ana.getTelefono());
        assertEquals("ana@correo.com", ana.getCorreo());
    }

    @Test
    void telefonoYCorreoSonOpcionales() {
        Cliente cliente = Cliente.registrar(Documento.de("ABC12345"), "Luis", " ", null);

        assertNull(cliente.getTelefono());
        assertNull(cliente.getCorreo());
    }

    @Test
    void validaDocumentoCorreoYTelefono() {
        assertThrows(ValorInvalidoException.class, () -> Documento.de("123"));
        assertThrows(ValorInvalidoException.class, () -> Documento.de("12 345 678"));
        assertThrows(ValorInvalidoException.class,
                () -> Cliente.registrar(Documento.de("1090123456"), "Ana", null, "no-es-correo"));
        assertThrows(ValorInvalidoException.class,
                () -> Cliente.registrar(Documento.de("1090123456"), "Ana", "abc", null));
        assertThrows(ValorInvalidoException.class,
                () -> Cliente.registrar(Documento.de("1090123456"), " ", null, null));
    }

    @Test
    void actualizarDatosNoCambiaElDocumento() {
        Cliente ana = Datos.ana();

        ana.actualizarDatos("Ana María Pérez", "3109998888", "ana.maria@correo.com");

        assertEquals("Ana María Pérez", ana.getNombre());
        assertEquals(Documento.de("1090123456"), ana.getDocumento());
    }
}
