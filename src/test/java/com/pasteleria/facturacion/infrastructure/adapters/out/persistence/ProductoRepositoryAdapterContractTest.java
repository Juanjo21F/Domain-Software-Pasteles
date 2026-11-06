package com.pasteleria.facturacion.infrastructure.adapters.out.persistence;

import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.contract.ProductoRepositoryContractTest;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import jakarta.persistence.EntityManager;
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
@Import(ProductoRepositoryAdapter.class)
class ProductoRepositoryAdapterContractTest extends ProductoRepositoryContractTest {

    @Autowired
    private ProductoRepositoryAdapter adaptador;
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

    /** Después de cada guardar se vacía la caché de Hibernate: así las lecturas van de verdad a la base de datos. */
    @Override
    protected ProductoRepositoryPort crearRepositorio() {
        return new ProductoRepositoryPort() {
            public Optional<Producto> buscarPorCodigo(CodigoProducto c) { return adaptador.buscarPorCodigo(c); }
            public boolean existePorCodigo(CodigoProducto c) { return adaptador.existePorCodigo(c); }
            public Producto guardar(Producto p) { Producto r = adaptador.guardar(p); em.flush(); em.clear(); return r; }
            public List<Producto> listar(boolean soloActivos) { return adaptador.listar(soloActivos); }
        };
    }
}