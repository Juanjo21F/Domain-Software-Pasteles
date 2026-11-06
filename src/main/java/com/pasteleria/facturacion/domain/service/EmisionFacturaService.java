package com.pasteleria.facturacion.domain.service;

import com.pasteleria.facturacion.domain.exception.ProductoInactivoException;
import com.pasteleria.facturacion.domain.exception.StockInsuficienteException;
import com.pasteleria.facturacion.domain.exception.StockInsuficienteException.Faltante;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.FacturaBuilder;
import com.pasteleria.facturacion.domain.model.ItemVenta;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de dominio (Semana 6): una regla que involucra varias entidades (Cliente, Producto, Factura)
 * y no le pertenece a ninguna sola. Es Java puro: no sabe de bases de datos ni de HTTP.
 *
 * Cubre HU-10 (factura con varios productos y total) y HU-11 (descuento de stock sin cambios parciales).
 */
public class EmisionFacturaService {

    /**
     * Precondición: los ítems que repiten un código comparten la MISMA instancia de Producto
     * (el caso de uso carga cada producto una sola vez).
     */
    public Factura emitir(Cliente cliente, List<ItemVenta> items, NumeroFactura numero, LocalDateTime fecha) {
        if (cliente == null) {
            throw new ValorInvalidoException("El cliente es obligatorio");
        }
        if (items == null || items.isEmpty()) {
            throw new ValorInvalidoException("La factura debe tener al menos un producto");
        }
        Map<CodigoProducto, ItemVenta> consolidado = consolidar(items);

        // Paso 1: validar TODO antes de tocar nada. Si algo falla, no queda ningún cambio a medias.
        List<Faltante> faltantes = new ArrayList<>();
        for (ItemVenta item : consolidado.values()) {
            if (!item.producto().isActivo()) {
                throw new ProductoInactivoException(item.producto().getCodigo());
            }
            if (!item.producto().puedeVender(item.cantidad())) {
                faltantes.add(new Faltante(item.producto().getCodigo(), item.producto().getNombre(),
                        item.producto().getStock(), item.cantidad()));
            }
        }
        if (!faltantes.isEmpty()) {
            throw new StockInsuficienteException(faltantes);
        }

        // Paso 2: armar la factura (puede fallar por datos incompletos, y todavía no se tocó el stock).
        FacturaBuilder builder = FacturaBuilder.nueva().numero(numero).fecha(fecha).cliente(cliente);
        consolidado.values().forEach(item -> builder.agregarLinea(item.producto(), item.cantidad()));
        Factura factura = builder.build();

        // Paso 3: recién ahora se descuenta el stock.
        consolidado.values().forEach(item -> item.producto().descontarStock(item.cantidad()));
        return factura;
    }

    private Map<CodigoProducto, ItemVenta> consolidar(List<ItemVenta> items) {
        Map<CodigoProducto, ItemVenta> mapa = new LinkedHashMap<>();
        for (ItemVenta item : items) {
            mapa.merge(item.producto().getCodigo(), item,
                    (a, b) -> new ItemVenta(a.producto(), a.cantidad() + b.cantidad()));
        }
        return mapa;
    }
}
