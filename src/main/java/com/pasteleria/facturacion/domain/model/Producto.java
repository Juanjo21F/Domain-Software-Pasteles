package com.pasteleria.facturacion.domain.model;

import com.pasteleria.facturacion.domain.exception.ProductoInactivoException;
import com.pasteleria.facturacion.domain.exception.StockInsuficienteException;
import com.pasteleria.facturacion.domain.exception.StockInsuficienteException.Faltante;
import com.pasteleria.facturacion.domain.exception.ValorInvalidoException;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import java.util.List;
import java.util.Objects;

/**
 * Entidad (Semana 6): tiene identidad (el código) y protege sus propias reglas.
 * Sin anotaciones de JPA ni de Spring (HU-7). La persistencia usa otra clase: ProductoJpaEntity.
 */
public class Producto {

    private static final int MAX_NOMBRE = 100;
    private static final int MAX_CATEGORIA = 50;

    private final CodigoProducto codigo;
    private String nombre;
    private String categoria;
    private Dinero precio;
    private int stock;
    private boolean activo;

    private Producto(CodigoProducto codigo, String nombre, String categoria, Dinero precio, int stock, boolean activo) {
        if (codigo == null) {
            throw new ValorInvalidoException("El código del producto es obligatorio");
        }
        if (stock < 0) {
            throw new ValorInvalidoException("El stock no puede ser negativo");
        }
        this.codigo = codigo;
        this.nombre = validarTexto(nombre, "El nombre", MAX_NOMBRE);
        this.categoria = validarTexto(categoria, "La categoría", MAX_CATEGORIA);
        this.precio = validarPrecio(precio);
        this.stock = stock;
        this.activo = activo;
    }

    /** Factory Method: un producto nuevo siempre nace activo. */
    public static Producto crear(CodigoProducto codigo, String nombre, String categoria, Dinero precio, int stockInicial) {
        return new Producto(codigo, nombre, categoria, precio, stockInicial, true);
    }

    /** Factory Method para la persistencia: rehidrata un producto ya existente. */
    public static Producto reconstruir(CodigoProducto codigo, String nombre, String categoria, Dinero precio,
                                       int stock, boolean activo) {
        return new Producto(codigo, nombre, categoria, precio, stock, activo);
    }

    public void modificar(String nombre, String categoria, Dinero precio) {
        this.nombre = validarTexto(nombre, "El nombre", MAX_NOMBRE);
        this.categoria = validarTexto(categoria, "La categoría", MAX_CATEGORIA);
        this.precio = validarPrecio(precio);
    }

    /** HU-8: un producto desactivado sale del catálogo de venta pero conserva su historia. Idempotente. */
    public void desactivar() {
        this.activo = false;
    }

    public boolean puedeVender(int cantidad) {
        return activo && cantidad > 0 && stock >= cantidad;
    }

    /** HU-11: nunca permite que el stock quede negativo, aunque alguien salte las validaciones previas. */
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ValorInvalidoException("La cantidad a descontar debe ser mayor que cero");
        }
        if (!activo) {
            throw new ProductoInactivoException(codigo);
        }
        if (stock < cantidad) {
            throw new StockInsuficienteException(List.of(new Faltante(codigo, nombre, stock, cantidad)));
        }
        stock -= cantidad;
    }

    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ValorInvalidoException("La cantidad a reponer debe ser mayor que cero");
        }
        stock += cantidad;
    }

    public CodigoProducto getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public Dinero getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public boolean isActivo() {
        return activo;
    }

    private static String validarTexto(String texto, String campo, int max) {
        if (texto == null || texto.isBlank()) {
            throw new ValorInvalidoException(campo + " es obligatorio");
        }
        String limpio = texto.trim();
        if (limpio.length() > max) {
            throw new ValorInvalidoException(campo + " no puede superar " + max + " caracteres");
        }
        return limpio;
    }

    private static Dinero validarPrecio(Dinero precio) {
        if (precio == null || !precio.esMayorQueCero()) {
            throw new ValorInvalidoException("El precio debe ser mayor que cero");
        }
        return precio;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Producto otro && codigo.equals(otro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
