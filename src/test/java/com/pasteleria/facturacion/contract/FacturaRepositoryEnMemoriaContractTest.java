package com.pasteleria.facturacion.contract;

import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.testsupport.FacturaRepositoryEnMemoria;

class FacturaRepositoryEnMemoriaContractTest extends FacturaRepositoryContractTest {

    @Override
    protected FacturaRepositoryPort crearRepositorio() {
        return new FacturaRepositoryEnMemoria();
    }
}
