package com.pasteleria.facturacion.infrastructure.adapters.out.persistence;

import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.FacturaEntity;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.mapper.FacturaMapper;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.repository.FacturaJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * La transacción está en el adaptador (infraestructura), no en el caso de uso: así la capa de aplicación
 * sigue sin importar nada de Spring y las líneas se leen dentro de una sesión abierta.
 */
@Component
@Transactional
public class FacturaRepositoryAdapter implements FacturaRepositoryPort {

    private final FacturaJpaRepository jpa;

    public FacturaRepositoryAdapter(FacturaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Factura> buscarPorNumero(NumeroFactura numero) {
        return jpa.findById(numero.valor()).map(FacturaMapper::aDominio);
    }

    @Override
    public Factura guardar(Factura factura) {
        Optional<FacturaEntity> existente = jpa.findById(factura.getNumero().valor());
        if (existente.isPresent()) {
            // Las líneas de una factura emitida no cambian nunca; lo único que puede variar es el estado (anulación).
            existente.get().setEstado(FacturaMapper.aDb(factura.getEstado()));
            return FacturaMapper.aDominio(jpa.saveAndFlush(existente.get()));
        }
        return FacturaMapper.aDominio(jpa.saveAndFlush(FacturaMapper.aEntidad(factura)));
    }

    @Override
    public List<Factura> buscarEmitidasEntre(LocalDateTime desdeInclusive, LocalDateTime hastaExclusive) {
        return jpa.buscarEntre(desdeInclusive, hastaExclusive).stream().map(FacturaMapper::aDominio).toList();
    }
}
