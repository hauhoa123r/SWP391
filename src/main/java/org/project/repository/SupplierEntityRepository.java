package org.project.repository;

import org.project.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierEntityRepository extends JpaRepository<SupplierEntity, Long> {
    List<SupplierEntity> findByNameContaining(String name);
    List<SupplierEntity> findByEmailStartingWith(String email);
}