package com.pasteleria.facturacion.application.port.out;

/** Entrega el siguiente consecutivo de factura del año, sin repetir ni saltar números. */
public interface NumeracionFacturaPort {

    long siguienteSecuencia(int anio);
}
