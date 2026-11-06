package com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto;

public record ClienteRequest(String documento, String nombre, String telefono, String correo) { }
