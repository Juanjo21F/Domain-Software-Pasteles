package com.pasteleria.facturacion.infrastructure.adapters.out.persistence;

import com.pasteleria.facturacion.application.port.out.ClienteRepositoryPort;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.vo.Documento;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.ClienteEntity;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.mapper.ClienteMapper;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.repository.ClienteJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpa;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(Documento documento) {
        return jpa.findById(documento.valor()).map(ClienteMapper::aDominio);
    }

    @Override
    public boolean existePorDocumento(Documento documento) {
        return jpa.existsById(documento.valor());
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entidad = jpa.findById(cliente.getDocumento().valor())
                .orElseGet(() -> new ClienteEntity(cliente.getDocumento().valor()));
        ClienteMapper.copiarA(entidad, cliente);
        return ClienteMapper.aDominio(jpa.saveAndFlush(entidad));
    }
}
