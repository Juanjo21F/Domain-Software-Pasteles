package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.repository;

import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.SecuenciaFacturaEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SecuenciaFacturaJpaRepository extends JpaRepository<SecuenciaFacturaEntity, Integer> {

    /** SELECT ... FOR UPDATE: quien pide el número bloquea la fila hasta que termine su transacción. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SecuenciaFacturaEntity s where s.anio = :anio")
    Optional<SecuenciaFacturaEntity> bloquearPorAnio(@Param("anio") int anio);
}
