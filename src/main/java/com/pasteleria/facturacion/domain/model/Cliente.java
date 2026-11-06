package com.pasteleria.facturacion.domain.model;

import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.vo.Documento;
import java.util.Objects;
import java.util.regex.Pattern;

/** Entidad cliente (HU-9). Identidad: el documento. Teléfono y correo son opcionales, pero si llegan se validan. */
public class Cliente {

    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    private final Documento documento;
    private String nombre;
    private String telefono;
    private String correo;

    private Cliente(Documento documento, String nombre, String telefono, String correo) {
        if (documento == null) {
            throw new ValorInvalidoException("El documento del cliente es obligatorio");
        }
        this.documento = documento;
        asignarDatos(nombre, telefono, correo);
    }

    public static Cliente registrar(Documento documento, String nombre, String telefono, String correo) {
        return new Cliente(documento, nombre, telefono, correo);
    }

    public static Cliente reconstruir(Documento documento, String nombre, String telefono, String correo) {
        return new Cliente(documento, nombre, telefono, correo);
    }

    /** El documento no cambia: es la identidad del cliente. */
    public void actualizarDatos(String nombre, String telefono, String correo) {
        asignarDatos(nombre, telefono, correo);
    }

    private void asignarDatos(String nombre, String telefono, String correo) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValorInvalidoException("El nombre del cliente es obligatorio");
        }
        String tel = vacioANulo(telefono);
        String mail = vacioANulo(correo);
        if (tel != null && !TELEFONO.matcher(tel).matches()) {
            throw new ValorInvalidoException("Teléfono inválido");
        }
        if (mail != null && !CORREO.matcher(mail).matches()) {
            throw new ValorInvalidoException("Correo electrónico inválido");
        }
        this.nombre = nombre.trim();
        this.telefono = tel;
        this.correo = mail;
    }

    private static String vacioANulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    public Documento getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Cliente otro && documento.equals(otro.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }
}
