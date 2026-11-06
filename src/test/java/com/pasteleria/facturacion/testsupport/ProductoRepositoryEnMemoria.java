package com.pasteleria.facturacion.testsupport;

import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Doble de prueba (Semana 5, DIP): reemplaza a la base de datos sin Spring ni JPA.
 * Guarda y entrega COPIAS, como haría una base real: si un caso de uso olvida llamar a guardar(),
 * la prueba lo detecta (con una referencia compartida el olvido pasaría desapercibido).
 */
public class ProductoRepositoryEnMemoria implements ProductoRepositoryPort {

    private final Map<CodigoProducto, Producto> almacen = new LinkedHashMap<>();

    @Override
    public Optional<Producto> buscarPorCodigo(CodigoProducto codigo) {
        return Optional.ofNullable(almacen.get(codigo)).map(ProductoRepositoryEnMemoria::copiar);
    }

    @Override
    public boolean existePorCodigo(CodigoProducto codigo) {
        return almacen.containsKey(codigo);
    }

    @Override
    public Producto guardar(Producto producto) {
        almacen.put(producto.getCodigo(), copiar(producto));
        return copiar(producto);
    }

    @Override
    public List<Producto> listar(boolean soloActivos) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : almacen.values()) {
            if (!soloActivos || p.isActivo()) {
                resultado.add(copiar(p));
            }
        }
        resultado.sort(Comparator.comparing(p -> p.getCodigo().valor()));
        return resultado;
    }

    private static Producto copiar(Producto p) {
        return Producto.reconstruir(p.getCodigo(), p.getNombre(), p.getCategoria(), p.getPrecio(), p.getStock(), p.isActivo());
    }
}
