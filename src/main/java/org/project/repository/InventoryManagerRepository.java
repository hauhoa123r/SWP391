package org.project.repository;

import org.project.entity.InventoryManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryManagerRepository extends JpaRepository<InventoryManagerEntity, Long> {
    Optional<InventoryManagerEntity> findByStaffEntity_Id(Long staffId);
} 