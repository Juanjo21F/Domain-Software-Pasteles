package com.pasteleria.facturacion.testsupport;

import com.pasteleria.facturacion.application.port.out.TransaccionPort;
import java.util.function.Supplier;

/** En las pruebas unitarias no hay base de datos: la "transacción" simplemente ejecuta el bloque. */
public class TransaccionDirecta implements TransaccionPort {

    @Override
    public <T> T ejecutar(Supplier<T> trabajo) {
        return trabajo.get();
    }
}
