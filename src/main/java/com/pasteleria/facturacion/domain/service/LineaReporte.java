package com.pasteleria.facturacion.domain.service;

import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;

/** Una fila del reporte mensual: producto, unidades vendidas e ingresos. */
public record LineaReporte(CodigoProducto codigo, String nombre, long cantidadVendida, Dinero ingresos) { }
