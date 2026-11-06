package com.pasteleria.facturacion.domain.model;

import com.pasteleria.facturacion.domain.exception.FacturaYaAnuladaException;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.domain.vo.Documento;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Agregado raíz (Semana 6). Las líneas no existen fuera de su factura y el total siempre se
 * calcula a partir de ellas: nadie puede dejar una factura con un total que no cuadra.
 * Una factura nueva solo se crea con FacturaBuilder (Semana 7).
 */
public class Factura {

    private final NumeroFactura numero;
    private final LocalDateTime fechaEmision;
    private final Documento clienteDocumento;
    private final List<LineaFactura> lineas;
    private EstadoFactura estado;

    // Visibilidad de paquete: solo FacturaBuilder y reconstruir() pueden construirla.
    Factura(NumeroFactura numero, LocalDateTime fechaEmision, Documento clienteDocumento,
            List<LineaFactura> lineas, EstadoFactura estado) {
        if (numero == null || fechaEmision == null || clienteDocumento == null || estado == null) {
            throw new ValorInvalidoException("La factura está incompleta");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw new ValorInvalidoException("La factura debe tener al menos un producto");
        }
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.clienteDocumento = clienteDocumento;
        this.lineas = new ArrayList<>(lineas);
        this.estado = estado;
    }

    /** Factory Method para la persistencia. */
    public static Factura reconstruir(NumeroFactura numero, LocalDateTime fechaEmision, Documento clienteDocumento,
                                      List<LineaFactura> lineas, EstadoFactura estado) {
        return new Factura(numero, fechaEmision, clienteDocumento, lineas, estado);
    }

    public Dinero total() {
        return lineas.stream().map(LineaFactura::subtotal).reduce(Dinero.CERO, Dinero::sumar);
    }

    public void anular() {
        if (estado == EstadoFactura.ANULADA) {
            throw new FacturaYaAnuladaException(numero);
        }
        estado = EstadoFactura.ANULADA;
    }

    public boolean estaAnulada() {
        return estado == EstadoFactura.ANULADA;
    }

    public NumeroFactura getNumero() {
        return numero;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public Documento getClienteDocumento() {
        return clienteDocumento;
    }

    public List<LineaFactura> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Factura otra && numero.equals(otra.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }
}
