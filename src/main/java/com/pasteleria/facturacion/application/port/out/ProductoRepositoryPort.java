package com.pasteleria.facturacion.application.port.out;

import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import java.util.List;
import java.util.Optional;

/** Patrón Repository (Semana 7): el caso de uso lo ve como una colección de productos, no como una base de datos. */
public interface ProductoRepositoryPort {

    Optional<Producto> buscarPorCodigo(CodigoProducto codigo);

    boolean existePorCodigo(CodigoProducto codigo);

    Producto guardar(Producto producto);

    List<Producto> listar(boolean soloActivos);
}
