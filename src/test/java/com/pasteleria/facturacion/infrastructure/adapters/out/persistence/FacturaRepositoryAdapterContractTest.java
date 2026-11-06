package com.pasteleria.facturacion.infrastructure.adapters.out.persistence;

import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.contract.FacturaRepositoryContractTest;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@DataJpaTest
@Import(FacturaRepositoryAdapter.class)
class FacturaRepositoryAdapterContractTest extends FacturaRepositoryContractTest {

    @Autowired
    private FacturaRepositoryAdapter adaptador;
    @Autowired
    private EntityManager em;

    // Los métodos de prueba se heredan de la clase de contrato (sin Spring), por eso @DataJpaTest no les abre
    // transacción. Se abre aquí a mano y se revierte al final, para que cada prueba quede aislada.
    @Autowired
    private PlatformTransactionManager gestorTransacciones;
    private TransactionStatus transaccion;

    @BeforeEach
    void abrirTransaccion() {
        transaccion = gestorTransacciones.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    void revertirTransaccion() {
        gestorTransacciones.rollback(transaccion);
    }

    @Override
    protected FacturaRepositoryPort crearRepositorio() {
        return new FacturaRepositoryPort() {
            public Optional<Factura> buscarPorNumero(NumeroFactura n) { return adaptador.buscarPorNumero(n); }
            public Factura guardar(Factura f) { Factura r = adaptador.guardar(f); em.flush(); em.clear(); return r; }
            public List<Factura> buscarEmitidasEntre(LocalDateTime d, LocalDateTime h) {
                return adaptador.buscarEmitidasEntre(d, h);
            }
        };
    }
}