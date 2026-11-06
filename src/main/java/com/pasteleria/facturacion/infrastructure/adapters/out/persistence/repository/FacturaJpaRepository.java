package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.repository;

import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.FacturaEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacturaJpaRepository extends JpaRepository<FacturaEntity, String> {

    @Query("select f from FacturaEntity f where f.fechaEmision >= :desde and f.fechaEmision < :hasta "
            + "order by f.fechaEmision asc, f.numero asc")
    List<FacturaEntity> buscarEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
