package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.mapper;

import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.ProductoEntity;

/** Traduce entre el modelo de dominio y el de base de datos. Ninguno de los dos conoce al otro. */
public final class ProductoMapper {

    private ProductoMapper() { }

    public static Producto aDominio(ProductoEntity e) {
        return Producto.reconstruir(CodigoProducto.de(e.getCodigo()), e.getNombre(), e.getCategoria(),
                Dinero.de(e.getPrecio()), e.getStock(), e.isActivo());
    }

    /** Copia el estado del dominio sobre una entidad (nueva o ya cargada, para conservar su @Version). */
    public static void copiarA(ProductoEntity e, Producto p) {
        e.setNombre(p.getNombre());
        e.setCategoria(p.getCategoria());
        e.setPrecio(p.getPrecio().valor());
        e.setStock(p.getStock());
        e.setActivo(p.isActivo());
    }
}
