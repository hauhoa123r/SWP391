package org.project.repository;

import org.project.entity.MedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for CRUD operations on {@link MedicineEntity}.
 */
@Repository
public interface MedicineRepository extends JpaRepository<MedicineEntity, Long> {
    MedicineEntity findByProductEntityId(Long productId);
    List<MedicineEntity> findByProductEntityNameContaining(String name);
}
