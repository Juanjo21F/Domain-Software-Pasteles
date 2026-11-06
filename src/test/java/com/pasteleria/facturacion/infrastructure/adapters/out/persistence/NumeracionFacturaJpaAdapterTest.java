package com.pasteleria.facturacion.infrastructure.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(NumeracionFacturaJpaAdapter.class)
class NumeracionFacturaJpaAdapterTest {

    @Autowired
    private NumeracionFacturaJpaAdapter numeracion;

    @Test
    @DisplayName("Consecutivo por año: 1, 2, 3... y cada año empieza de nuevo")
    void consecutivoPorAnio() {
        assertEquals(1, numeracion.siguienteSecuencia(2026));
        assertEquals(2, numeracion.siguienteSecuencia(2026));
        assertEquals(3, numeracion.siguienteSecuencia(2026));
        assertEquals(1, numeracion.siguienteSecuencia(2027));
        assertEquals(4, numeracion.siguienteSecuencia(2026));
    }
}
