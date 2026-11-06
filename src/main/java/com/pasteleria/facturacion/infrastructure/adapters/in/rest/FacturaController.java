package com.pasteleria.facturacion.infrastructure.adapters.in.rest;

import com.pasteleria.facturacion.application.port.in.AnularFacturaUseCase;
import com.pasteleria.facturacion.application.port.in.ConsultarFacturaUseCase;
import com.pasteleria.facturacion.application.port.in.EmitirFacturaUseCase;
import com.pasteleria.facturacion.application.port.in.command.EmitirFacturaCommand;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.FacturaRequest;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.FacturaResponse;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** HU-10 y HU-11: emitir facturas (con descuento de stock) y anularlas. */
@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final EmitirFacturaUseCase emitir;
    private final AnularFacturaUseCase anular;
    private final ConsultarFacturaUseCase consultar;

    public FacturaController(EmitirFacturaUseCase emitir, AnularFacturaUseCase anular, ConsultarFacturaUseCase consultar) {
        this.emitir = emitir;
        this.anular = anular;
        this.consultar = consultar;
    }

    @PostMapping
    public ResponseEntity<FacturaResponse> emitir(@RequestBody FacturaRequest r) {
        List<EmitirFacturaCommand.Linea> lineas = r.lineas() == null ? List.of() : r.lineas().stream()
                .map(l -> new EmitirFacturaCommand.Linea(l.codigoProducto(), l.cantidad()))
                .toList();
        var factura = emitir.emitir(new EmitirFacturaCommand(r.clienteDocumento(), lineas));
        return ResponseEntity.created(URI.create("/api/facturas/" + factura.getNumero().valor()))
                .body(FacturaResponse.de(factura));
    }

    @GetMapping("/{numero}")
    public FacturaResponse obtener(@PathVariable String numero) {
        return FacturaResponse.de(consultar.porNumero(numero));
    }

    @PostMapping("/{numero}/anulacion")
    public FacturaResponse anular(@PathVariable String numero) {
        return FacturaResponse.de(anular.anular(numero));
    }
}
