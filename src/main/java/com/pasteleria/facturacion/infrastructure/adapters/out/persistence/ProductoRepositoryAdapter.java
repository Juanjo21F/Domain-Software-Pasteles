package com.pasteleria.facturacion.infrastructure.adapters.out.persistence;

import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.domain.model.Producto;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.ProductoEntity;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.mapper.ProductoMapper;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.repository.ProductoJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository jpa;

    public ProductoRepositoryAdapter(ProductoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Producto> buscarPorCodigo(CodigoProducto codigo) {
        return jpa.findById(codigo.valor()).map(ProductoMapper::aDominio);
    }

    @Override
    public boolean existePorCodigo(CodigoProducto codigo) {
        return jpa.existsById(codigo.valor());
    }

    @Override
    public Producto guardar(Producto producto) {
        // Si ya existe se reutiliza la entidad cargada: así Hibernate conserva su @Version y detecta ventas simultáneas.
        ProductoEntity entidad = jpa.findById(producto.getCodigo().valor())
                .orElseGet(() -> new ProductoEntity(producto.getCodigo().valor()));
        ProductoMapper.copiarA(entidad, producto);
        return ProductoMapper.aDominio(jpa.saveAndFlush(entidad));
    }

    @Override
    public List<Producto> listar(boolean soloActivos) {
        List<ProductoEntity> entidades = soloActivos ? jpa.findByActivoTrueOrderByCodigoAsc() : jpa.findAllByOrderByCodigoAsc();
        return entidades.stream().map(ProductoMapper::aDominio).toList();
    }
}
