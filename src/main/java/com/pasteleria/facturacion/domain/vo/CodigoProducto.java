package com.pasteleria.facturacion.domain.vo;

import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import java.util.Locale;
import java.util.regex.Pattern;

/** Identificador de negocio del producto (HU-8: código único). Se normaliza a mayúsculas. */
public record CodigoProducto(String valor) {

    private static final Pattern FORMATO = Pattern.compile("[A-Z0-9][A-Z0-9-]{1,19}");

    public CodigoProducto {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalidoException("El código del producto es obligatorio");
        }
        valor = valor.trim().toUpperCase(Locale.ROOT);
        if (!FORMATO.matcher(valor).matches()) {
            throw new ValorInvalidoException(
                    "Código de producto inválido: use de 2 a 20 caracteres entre letras, números y guion");
        }
    }

    public static CodigoProducto de(String valor) {
        return new CodigoProducto(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
