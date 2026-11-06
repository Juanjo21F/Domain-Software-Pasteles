package com.pasteleria.facturacion.domain.exception;

import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import java.util.List;
import java.util.stream.Collectors;

/** HU-11: la venta se cancela y se informa TODOS los productos que no alcanzan, no solo el primero. */
public class StockInsuficienteException extends DominioException {

    private final List<Faltante> faltantes;

    public StockInsuficienteException(List<Faltante> faltantes) {
        super("Stock insuficiente: " + describir(faltantes));
        this.faltantes = List.copyOf(faltantes);
    }

    public List<Faltante> getFaltantes() {
        return faltantes;
    }

    public record Faltante(CodigoProducto codigo, String nombre, int disponible, int solicitado) { }

    private static String describir(List<Faltante> faltantes) {
        return faltantes.stream()
                .map(f -> f.codigo().valor() + " (disponible " + f.disponible() + ", solicitado " + f.solicitado() + ")")
                .collect(Collectors.joining(", "));
    }
}
