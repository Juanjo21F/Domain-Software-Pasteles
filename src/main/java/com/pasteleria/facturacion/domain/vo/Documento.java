package com.pasteleria.facturacion.domain.vo;

import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import java.util.Locale;
import java.util.regex.Pattern;

/** Documento de identidad del cliente (HU-9: único). Alfanumérico de 5 a 20 caracteres. */
public record Documento(String valor) {

    private static final Pattern FORMATO = Pattern.compile("[A-Z0-9]{5,20}");

    public Documento {
        if (valor == null || valor.isBlank()) {
            throw new ValorInvalidoException("El documento del cliente es obligatorio");
        }
        valor = valor.trim().toUpperCase(Locale.ROOT);
        if (!FORMATO.matcher(valor).matches()) {
            throw new ValorInvalidoException("Documento inválido: use de 5 a 20 letras o números, sin espacios");
        }
    }

    public static Documento de(String valor) {
        return new Documento(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
