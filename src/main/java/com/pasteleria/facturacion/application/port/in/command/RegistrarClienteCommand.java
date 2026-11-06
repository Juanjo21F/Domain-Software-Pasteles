package com.pasteleria.facturacion.application.port.in.command;

public record RegistrarClienteCommand(String documento, String nombre, String telefono, String correo) { }
