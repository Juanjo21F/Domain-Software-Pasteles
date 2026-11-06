package com.pasteleria.facturacion.application.service;

import com.pasteleria.facturacion.application.port.in.AnularFacturaUseCase;
import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.application.port.out.TransaccionPort;
import com.pasteleria.facturacion.domain.exception.FacturaNoEncontradaException;
import com.pasteleria.facturacion.domain.exception.ProductoNoEncontradoException;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.LineaFactura;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;

/**
 * Anula una factura y devuelve el stock de sus productos.
 * Decisión asumida (el backlog no la define): una factura anulada NO se vendió, así que el stock se restituye.
 */
public class AnularFacturaService implements AnularFacturaUseCase {

    private final FacturaRepositoryPort facturas;
    private final ProductoRepositoryPort productos;
    private final TransaccionPort transaccion;

    public AnularFacturaService(FacturaRepositoryPort facturas, ProductoRepositoryPort productos,
                                TransaccionPort transaccion) {
        this.facturas = facturas;
        this.productos = productos;
        this.transaccion = transaccion;
    }

    @Override
    public Factura anular(String numeroFactura) {
        NumeroFactura numero = NumeroFactura.desde(numeroFactura);
        return transaccion.ejecutar(() -> {
            Factura factura = facturas.buscarPorNumero(numero)
                    .orElseThrow(() -> new FacturaNoEncontradaException(numero));

            factura.anular(); // falla si ya estaba anulada: así el stock no se repone dos veces

            for (LineaFactura linea : factura.getLineas()) {
                Producto producto = productos.buscarPorCodigo(linea.codigoProducto())
                        .orElseThrow(() -> new ProductoNoEncontradoException(linea.codigoProducto()));
                producto.reponerStock(linea.cantidad());
                productos.guardar(producto);
            }
            return facturas.guardar(factura);
        });
    }
}
