package com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto;

import com.pasteleria.facturacion.domain.model.Producto;
import java.math.BigDecimal;

public record ProductoResponse(String codigo, String nombre, String categoria, BigDecimal precio, int stock, boolean activo) {

    public static ProductoResponse de(Producto p) {
        return new ProductoResponse(p.getCodigo().valor(), p.getNombre(), p.getCategoria(),
                p.getPrecio().valor(), p.getStock(), p.isActivo());
    }
}
