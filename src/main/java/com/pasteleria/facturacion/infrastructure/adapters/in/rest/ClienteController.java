package com.pasteleria.facturacion.infrastructure.adapters.in.rest;

import com.pasteleria.facturacion.application.port.in.ActualizarClienteUseCase;
import com.pasteleria.facturacion.application.port.in.ConsultarClienteUseCase;
import com.pasteleria.facturacion.application.port.in.RegistrarClienteUseCase;
import com.pasteleria.facturacion.application.port.in.command.ActualizarClienteCommand;
import com.pasteleria.facturacion.application.port.in.command.RegistrarClienteCommand;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.ClienteRequest;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.ClienteResponse;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.ClienteUpdateRequest;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HU-9: gestión de clientes. */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final RegistrarClienteUseCase registrar;
    private final ActualizarClienteUseCase actualizar;
    private final ConsultarClienteUseCase consultar;

    public ClienteController(RegistrarClienteUseCase registrar, ActualizarClienteUseCase actualizar,
                             ConsultarClienteUseCase consultar) {
        this.registrar = registrar;
        this.actualizar = actualizar;
        this.consultar = consultar;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@RequestBody ClienteRequest r) {
        var cliente = registrar.registrar(new RegistrarClienteCommand(r.documento(), r.nombre(), r.telefono(), r.correo()));
        return ResponseEntity.created(URI.create("/api/clientes/" + cliente.getDocumento().valor()))
                .body(ClienteResponse.de(cliente));
    }

    @GetMapping("/{documento}")
    public ClienteResponse obtener(@PathVariable String documento) {
        return ClienteResponse.de(consultar.porDocumento(documento));
    }

    @PutMapping("/{documento}")
    public ClienteResponse actualizar(@PathVariable String documento, @RequestBody ClienteUpdateRequest r) {
        return ClienteResponse.de(actualizar.actualizar(
                new ActualizarClienteCommand(documento, r.nombre(), r.telefono(), r.correo())));
    }
}
