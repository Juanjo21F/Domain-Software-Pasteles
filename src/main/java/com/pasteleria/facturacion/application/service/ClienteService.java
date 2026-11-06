package com.pasteleria.facturacion.application.service;

import com.pasteleria.facturacion.application.port.in.ActualizarClienteUseCase;
import com.pasteleria.facturacion.application.port.in.ConsultarClienteUseCase;
import com.pasteleria.facturacion.application.port.in.RegistrarClienteUseCase;
import com.pasteleria.facturacion.application.port.in.command.ActualizarClienteCommand;
import com.pasteleria.facturacion.application.port.in.command.RegistrarClienteCommand;
import com.pasteleria.facturacion.application.port.out.ClienteRepositoryPort;
import com.pasteleria.facturacion.domain.exception.ClienteDuplicadoException;
import com.pasteleria.facturacion.domain.exception.ClienteNoEncontradoException;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.vo.Documento;

/** HU-9: registrar, actualizar y buscar clientes por documento. */
public class ClienteService implements RegistrarClienteUseCase, ActualizarClienteUseCase, ConsultarClienteUseCase {

    private final ClienteRepositoryPort clientes;

    public ClienteService(ClienteRepositoryPort clientes) {
        this.clientes = clientes;
    }

    @Override
    public Cliente registrar(RegistrarClienteCommand comando) {
        Documento documento = Documento.de(comando.documento());
        if (clientes.existePorDocumento(documento)) {
            throw new ClienteDuplicadoException(documento);
        }
        Cliente cliente = Cliente.registrar(documento, comando.nombre(), comando.telefono(), comando.correo());
        return clientes.guardar(cliente);
    }

    @Override
    public Cliente actualizar(ActualizarClienteCommand comando) {
        Cliente cliente = buscar(Documento.de(comando.documento()));
        cliente.actualizarDatos(comando.nombre(), comando.telefono(), comando.correo());
        return clientes.guardar(cliente);
    }

    @Override
    public Cliente porDocumento(String documento) {
        return buscar(Documento.de(documento));
    }

    private Cliente buscar(Documento documento) {
        return clientes.buscarPorDocumento(documento).orElseThrow(() -> new ClienteNoEncontradoException(documento));
    }
}
