package com.pasteleria.facturacion.application.port.in.command;

import java.math.BigDecimal;

public record RegistrarProductoCommand(String codigo, String nombre, String categoria, BigDecimal precio, int stock) { }
