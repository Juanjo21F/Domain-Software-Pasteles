package com.pasteleria.facturacion.application.service;

import com.pasteleria.facturacion.application.port.in.ConsultarProductoUseCase;
import com.pasteleria.facturacion.application.port.in.DesactivarProductoUseCase;
import com.pasteleria.facturacion.application.port.in.ModificarProductoUseCase;
import com.pasteleria.facturacion.application.port.in.RegistrarProductoUseCase;
import com.pasteleria.facturacion.application.port.in.command.ModificarProductoCommand;
import com.pasteleria.facturacion.application.port.in.command.RegistrarProductoCommand;
import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.domain.exception.ProductoDuplicadoException;
import com.pasteleria.facturacion.domain.exception.ProductoNoEncontradoException;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import java.util.List;

/**
 * HU-8. Una sola clase implementa los cuatro puertos de entrada del catálogo porque todos hablan
 * con el mismo repositorio; cada controlador, en cambio, solo ve el puerto que necesita (ISP).
 */
public class ProductoService implements RegistrarProductoUseCase, ModificarProductoUseCase,
        DesactivarProductoUseCase, ConsultarProductoUseCase {

    private final ProductoRepositoryPort productos;

    public ProductoService(ProductoRepositoryPort productos) {
        this.productos = productos;
    }

    @Override
    public Producto registrar(RegistrarProductoCommand comando) {
        CodigoProducto codigo = CodigoProducto.de(comando.codigo());
        if (productos.existePorCodigo(codigo)) {
            throw new ProductoDuplicadoException(codigo);
        }
        Producto producto = Producto.crear(codigo, comando.nombre(), comando.categoria(),
                Dinero.de(comando.precio()), comando.stock());
        return productos.guardar(producto);
    }

    @Override
    public Producto modificar(ModificarProductoCommand comando) {
        Producto producto = buscar(CodigoProducto.de(comando.codigo()));
        producto.modificar(comando.nombre(), comando.categoria(), Dinero.de(comando.precio()));
        return productos.guardar(producto);
    }

    @Override
    public Producto desactivar(String codigo) {
        Producto producto = buscar(CodigoProducto.de(codigo));
        producto.desactivar();
        return productos.guardar(producto);
    }

    @Override
    public Producto porCodigo(String codigo) {
        return buscar(CodigoProducto.de(codigo));
    }

    @Override
    public List<Producto> listar(boolean soloActivos) {
        return productos.listar(soloActivos);
    }

    private Producto buscar(CodigoProducto codigo) {
        return productos.buscarPorCodigo(codigo).orElseThrow(() -> new ProductoNoEncontradoException(codigo));
    }
}
