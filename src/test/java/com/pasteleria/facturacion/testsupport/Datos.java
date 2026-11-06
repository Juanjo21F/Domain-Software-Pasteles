package com.pasteleria.facturacion.testsupport;

import com.pasteleria.facturacion.domain.model.Cliente;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.domain.vo.Documento;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/** Objetos de ejemplo para las pruebas. */
public final class Datos {

    public static final ZoneId BOGOTA = ZoneId.of("America/Bogota");

    private Datos() { }

    /** 20 de octubre de 2026, 10:00 hora de Bogotá. */
    public static Clock relojFijo() {
        return Clock.fixed(Instant.parse("2026-10-20T15:00:00Z"), BOGOTA);
    }

    public static Producto tortaChocolate(int stock) {
        return Producto.crear(CodigoProducto.de("TOR-001"), "Torta de chocolate", "Tortas", Dinero.de(45000), stock);
    }

    public static Producto cupcake(int stock) {
        return Producto.crear(CodigoProducto.de("CUP-001"), "Cupcake de vainilla", "Cupcakes", Dinero.de(6500), stock);
    }

    public static Producto galleta(int stock) {
        return Producto.crear(CodigoProducto.de("GAL-001"), "Galleta de avena", "Galletas", Dinero.de(2500), stock);
    }

    public static Cliente ana() {
        return Cliente.registrar(Documento.de("1090123456"), "Ana Pérez", "3001234567", "ana@correo.com");
    }
}
