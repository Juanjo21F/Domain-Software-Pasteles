package com.pasteleria.facturacion.infrastructure.adapters.out.persistence;

import com.pasteleria.facturacion.application.port.out.NumeracionFacturaPort;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.SecuenciaFacturaEntity;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.repository.SecuenciaFacturaJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NumeracionFacturaJpaAdapter implements NumeracionFacturaPort {

    private final SecuenciaFacturaJpaRepository jpa;

    public NumeracionFacturaJpaAdapter(SecuenciaFacturaJpaRepository jpa) {
        this.jpa = jpa;
    }

    /**
     * Si la venta se cancela, la transacción se revierte y el número no se consume (no hay huecos).
     * Limitación conocida: si dos ventas piden el PRIMER número de un año exactamente a la vez,
     * una puede fallar con un conflicto de llave y debe reintentarse.
     */
    @Override
    public long siguienteSecuencia(int anio) {
        SecuenciaFacturaEntity fila = jpa.bloquearPorAnio(anio).orElse(null);
        if (fila == null) {
            return jpa.saveAndFlush(new SecuenciaFacturaEntity(anio, 1)).getUltimo();
        }
        fila.setUltimo(fila.getUltimo() + 1);
        return jpa.saveAndFlush(fila).getUltimo();
    }
}
