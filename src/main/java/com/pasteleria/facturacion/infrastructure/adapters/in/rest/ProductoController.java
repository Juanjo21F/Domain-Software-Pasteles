package com.pasteleria.facturacion.infrastructure.adapters.in.rest;

import com.pasteleria.facturacion.application.port.in.ConsultarProductoUseCase;
import com.pasteleria.facturacion.application.port.in.DesactivarProductoUseCase;
import com.pasteleria.facturacion.application.port.in.ModificarProductoUseCase;
import com.pasteleria.facturacion.application.port.in.RegistrarProductoUseCase;
import com.pasteleria.facturacion.application.port.in.command.ModificarProductoCommand;
import com.pasteleria.facturacion.application.port.in.command.RegistrarProductoCommand;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.ProductoRequest;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.ProductoResponse;
import com.pasteleria.facturacion.infrastructure.adapters.in.rest.dto.ProductoUpdateRequest;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** HU-8: gestión de productos. */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final RegistrarProductoUseCase registrar;
    private final ModificarProductoUseCase modificar;
    private final DesactivarProductoUseCase desactivar;
    private final ConsultarProductoUseCase consultar;

    public ProductoController(RegistrarProductoUseCase registrar, ModificarProductoUseCase modificar,
                              DesactivarProductoUseCase desactivar, ConsultarProductoUseCase consultar) {
        this.registrar = registrar;
        this.modificar = modificar;
        this.desactivar = desactivar;
        this.consultar = consultar;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest r) {
        var producto = registrar.registrar(new RegistrarProductoCommand(
                r.codigo(), r.nombre(), r.categoria(), r.precio(), r.stock()));
        return ResponseEntity.created(URI.create("/api/productos/" + producto.getCodigo().valor()))
                .body(ProductoResponse.de(producto));
    }

    @GetMapping
    public List<ProductoResponse> listar(@RequestParam(defaultValue = "false") boolean soloActivos) {
        return consultar.listar(soloActivos).stream().map(ProductoResponse::de).toList();
    }

    @GetMapping("/{codigo}")
    public ProductoResponse obtener(@PathVariable String codigo) {
        return ProductoResponse.de(consultar.porCodigo(codigo));
    }

    @PutMapping("/{codigo}")
    public ProductoResponse modificar(@PathVariable String codigo, @RequestBody ProductoUpdateRequest r) {
        return ProductoResponse.de(modificar.modificar(
                new ModificarProductoCommand(codigo, r.nombre(), r.categoria(), r.precio())));
    }

    @PatchMapping("/{codigo}/desactivar")
    public ProductoResponse desactivar(@PathVariable String codigo) {
        return ProductoResponse.de(desactivar.desactivar(codigo));
    }
}
