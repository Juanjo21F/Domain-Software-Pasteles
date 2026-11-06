package com.pasteleria.facturacion.application.service;

import com.pasteleria.facturacion.application.port.in.EmitirFacturaUseCase;
import com.pasteleria.facturacion.application.port.in.command.EmitirFacturaCommand;
import com.pasteleria.facturacion.application.port.out.ClienteRepositoryPort;
import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.application.port.out.NumeracionFacturaPort;
import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.application.port.out.TransaccionPort;
import com.pasteleria.facturacion.domain.exception.ClienteNoEncontradoException;
import com.pasteleria.facturacion.domain.exception.ProductoNoEncontradoException;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.ItemVenta;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.service.EmisionFacturaService;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Documento;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HU-10 y HU-11. Este caso de uso solo ORQUESTA: carga lo que necesita, delega la regla al servicio
 * de dominio y guarda el resultado. No calcula totales ni valida stock (SRP).
 * El reloj entra por constructor (DIP): las pruebas usan un Clock fijo.
 */
public class EmitirFacturaService implements EmitirFacturaUseCase {

    private final ClienteRepositoryPort clientes;
    private final ProductoRepositoryPort productos;
    private final FacturaRepositoryPort facturas;
    private final NumeracionFacturaPort numeracion;
    private final TransaccionPort transaccion;
    private final Clock reloj;
    private final EmisionFacturaService emision;

    public EmitirFacturaService(ClienteRepositoryPort clientes, ProductoRepositoryPort productos,
                                FacturaRepositoryPort facturas, NumeracionFacturaPort numeracion,
                                TransaccionPort transaccion, Clock reloj, EmisionFacturaService emision) {
        this.clientes = clientes;
        this.productos = productos;
        this.facturas = facturas;
        this.numeracion = numeracion;
        this.transaccion = transaccion;
        this.reloj = reloj;
        this.emision = emision;
    }

    @Override
    public Factura emitir(EmitirFacturaCommand comando) {
        if (comando.lineas() == null || comando.lineas().isEmpty()) {
            throw new ValorInvalidoException("La factura debe tener al menos un producto");
        }
        Documento documento = Documento.de(comando.clienteDocumento());

        // Todo dentro de una transacción: si algo falla al guardar, no queda ningún cambio parcial.
        return transaccion.ejecutar(() -> {
            Cliente cliente = clientes.buscarPorDocumento(documento)
                    .orElseThrow(() -> new ClienteNoEncontradoException(documento));

            // Cada producto se carga UNA vez, aunque aparezca en varias líneas del pedido.
            Map<CodigoProducto, Producto> cargados = new LinkedHashMap<>();
            List<ItemVenta> items = new ArrayList<>();
            for (EmitirFacturaCommand.Linea linea : comando.lineas()) {
                CodigoProducto codigo = CodigoProducto.de(linea.codigoProducto());
                Producto producto = cargados.get(codigo);
                if (producto == null) {
                    producto = productos.buscarPorCodigo(codigo)
                            .orElseThrow(() -> new ProductoNoEncontradoException(codigo));
                    cargados.put(codigo, producto);
                }
                items.add(new ItemVenta(producto, linea.cantidad()));
            }

            LocalDateTime ahora = LocalDateTime.now(reloj);
            NumeroFactura numero = NumeroFactura.de(ahora.getYear(), numeracion.siguienteSecuencia(ahora.getYear()));

            Factura factura = emision.emitir(cliente, items, numero, ahora);

            cargados.values().forEach(productos::guardar);
            return facturas.guardar(factura);
        });
    }
}
