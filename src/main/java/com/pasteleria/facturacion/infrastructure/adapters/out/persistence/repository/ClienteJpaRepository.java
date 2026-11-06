package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.repository;

import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, String> { }
