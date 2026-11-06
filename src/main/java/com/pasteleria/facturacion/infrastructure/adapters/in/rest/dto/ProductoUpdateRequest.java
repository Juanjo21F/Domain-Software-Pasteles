package com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;

public record ProductoUpdateRequest(String nombre, String categoria, BigDecimal precio) { }
