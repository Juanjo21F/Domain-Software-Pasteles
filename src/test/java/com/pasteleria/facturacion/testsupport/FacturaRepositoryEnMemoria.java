package com.pasteleria.facturacion.testsupport;

import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FacturaRepositoryEnMemoria implements FacturaRepositoryPort {

    private final Map<NumeroFactura, Factura> almacen = new LinkedHashMap<>();

    @Override
    public Optional<Factura> buscarPorNumero(NumeroFactura numero) {
        return Optional.ofNullable(almacen.get(numero)).map(FacturaRepositoryEnMemoria::copiar);
    }

    @Override
    public Factura guardar(Factura factura) {
        almacen.put(factura.getNumero(), copiar(factura));
        return copiar(factura);
    }

    @Override
    public List<Factura> buscarEmitidasEntre(LocalDateTime desdeInclusive, LocalDateTime hastaExclusive) {
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : almacen.values()) {
            boolean dentro = !f.getFechaEmision().isBefore(desdeInclusive) && f.getFechaEmision().isBefore(hastaExclusive);
            if (dentro) {
                resultado.add(copiar(f));
            }
        }
        resultado.sort(Comparator.comparing(Factura::getFechaEmision));
        return resultado;
    }

    public int cantidad() {
        return almacen.size();
    }

    private static Factura copiar(Factura f) {
        return Factura.reconstruir(f.getNumero(), f.getFechaEmision(), f.getClienteDocumento(), f.getLineas(), f.getEstado());
    }
}
