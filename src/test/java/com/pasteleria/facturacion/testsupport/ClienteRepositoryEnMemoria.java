package com.pasteleria.facturacion.testsupport;

import com.pasteleria.facturacion.application.port.out.ClienteRepositoryPort;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.vo.Documento;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ClienteRepositoryEnMemoria implements ClienteRepositoryPort {

    private final Map<Documento, Cliente> almacen = new LinkedHashMap<>();

    @Override
    public Optional<Cliente> buscarPorDocumento(Documento documento) {
        return Optional.ofNullable(almacen.get(documento)).map(ClienteRepositoryEnMemoria::copiar);
    }

    @Override
    public boolean existePorDocumento(Documento documento) {
        return almacen.containsKey(documento);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        almacen.put(cliente.getDocumento(), copiar(cliente));
        return copiar(cliente);
    }

    public int cantidad() {
        return almacen.size();
    }

    private static Cliente copiar(Cliente c) {
        return Cliente.reconstruir(c.getDocumento(), c.getNombre(), c.getTelefono(), c.getCorreo());
    }
}
