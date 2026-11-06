package com.pasteleria.facturacion.application.service;

import com.pasteleria.facturacion.application.port.in.ConsultarFacturaUseCase;
import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.domain.exception.FacturaNoEncontradaException;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;

public class ConsultarFacturaService implements ConsultarFacturaUseCase {

    private final FacturaRepositoryPort facturas;

    public ConsultarFacturaService(FacturaRepositoryPort facturas) {
        this.facturas = facturas;
    }

    @Override
    public Factura porNumero(String numeroFactura) {
        NumeroFactura numero = NumeroFactura.desde(numeroFactura);
        return facturas.buscarPorNumero(numero).orElseThrow(() -> new FacturaNoEncontradaException(numero));
    }
}
