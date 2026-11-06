package com.pasteleria.facturacion.infrastructure.adapters.out.persistence;

import com.pasteleria.facturacion.application.port.out.ClienteRepositoryPort;
import com.pasteleria.facturacion.contract.ClienteRepositoryContractTest;
import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.vo.Documento;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(ClienteRepositoryAdapter.class)
class ClienteRepositoryAdapterContractTest extends ClienteRepositoryContractTest {

    @Autowired
    private ClienteRepositoryAdapter adaptador;
    @Autowired
    private EntityManager em;

    @Override
    protected ClienteRepositoryPort crearRepositorio() {
        return new ClienteRepositoryPort() {
            public Optional<Cliente> buscarPorDocumento(Documento d) { return adaptador.buscarPorDocumento(d); }
            public boolean existePorDocumento(Documento d) { return adaptador.existePorDocumento(d); }
            public Cliente guardar(Cliente c) { Cliente r = adaptador.guardar(c); em.flush(); em.clear(); return r; }
        };
    }
}
