package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.repository;

import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.ProductoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, String> {

    List<ProductoEntity> findAllByOrderByCodigoAsc();

    List<ProductoEntity> findByActivoTrueOrderByCodigoAsc();
}
