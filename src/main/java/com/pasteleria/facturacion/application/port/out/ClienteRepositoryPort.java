package com.pasteleria.facturacion.application.port.out;

import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.vo.Documento;
import java.util.Optional;

public interface ClienteRepositoryPort {

    Optional<Cliente> buscarPorDocumento(Documento documento);

    boolean existePorDocumento(Documento documento);

    Cliente guardar(Cliente cliente);
}
