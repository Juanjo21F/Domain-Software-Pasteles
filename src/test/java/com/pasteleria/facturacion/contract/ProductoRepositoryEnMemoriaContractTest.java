package com.pasteleria.facturacion.contract;

import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.testsupport.ProductoRepositoryEnMemoria;

class ProductoRepositoryEnMemoriaContractTest extends ProductoRepositoryContractTest {

    @Override
    protected ProductoRepositoryPort crearRepositorio() {
        return new ProductoRepositoryEnMemoria();
    }
}
