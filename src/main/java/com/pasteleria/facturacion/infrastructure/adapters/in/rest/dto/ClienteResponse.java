package com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto;

import com.pasteleria.facturacion.domain.model.Cliente;

public record ClienteResponse(String documento, String nombre, String telefono, String correo) {

    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getDocumento().valor(), c.getNombre(), c.getTelefono(), c.getCorreo());
    }
}
