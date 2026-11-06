package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.mapper;

import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.vo.Documento;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.ClienteEntity;

public final class ClienteMapper {

    private ClienteMapper() { }

    public static Cliente aDominio(ClienteEntity e) {
        return Cliente.reconstruir(Documento.de(e.getDocumento()), e.getNombre(), e.getTelefono(), e.getCorreo());
    }

    public static void copiarA(ClienteEntity e, Cliente c) {
        e.setNombre(c.getNombre());
        e.setTelefono(c.getTelefono());
        e.setCorreo(c.getCorreo());
    }
}
