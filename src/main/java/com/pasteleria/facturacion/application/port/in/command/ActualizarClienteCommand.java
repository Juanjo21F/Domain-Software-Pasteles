package com.pasteleria.facturacion.application.port.in.command;

public record ActualizarClienteCommand(String documento, String nombre, String telefono, String correo) { }
