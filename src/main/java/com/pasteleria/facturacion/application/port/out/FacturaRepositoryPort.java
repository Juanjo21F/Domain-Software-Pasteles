package com.pasteleria.facturacion.application.port.out;

import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FacturaRepositoryPort {

    Optional<Factura> buscarPorNumero(NumeroFactura numero);

    Factura guardar(Factura factura);

    /** Todas las facturas del rango [desde, hasta), anuladas incluidas: filtrarlas es regla de dominio. */
    List<Factura> buscarEmitidasEntre(LocalDateTime desdeInclusive, LocalDateTime hastaExclusive);
}
