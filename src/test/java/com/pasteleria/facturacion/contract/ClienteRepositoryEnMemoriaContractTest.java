package com.pasteleria.facturacion.contract;

import com.pasteleria.facturacion.application.port.out.ClienteRepositoryPort;
import com.pasteleria.facturacion.testsupport.ClienteRepositoryEnMemoria;

class ClienteRepositoryEnMemoriaContractTest extends ClienteRepositoryContractTest {

    @Override
    protected ClienteRepositoryPort crearRepositorio() {
        return new ClienteRepositoryEnMemoria();
    }
}
