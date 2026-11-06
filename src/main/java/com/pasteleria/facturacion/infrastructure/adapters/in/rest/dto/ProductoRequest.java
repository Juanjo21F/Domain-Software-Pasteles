package com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;

public record ProductoRequest(String codigo, String nombre, String categoria, BigDecimal precio, int stock) { }
