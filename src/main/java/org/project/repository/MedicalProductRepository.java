package org.project.repository;

import org.project.entity.MedicalProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for CRUD operations on {@link MedicalProductEntity}.
 */
@Repository
public interface MedicalProductRepository extends JpaRepository<MedicalProductEntity, Long> {
    MedicalProductEntity findByProductEntityId(Long productId);
    List<MedicalProductEntity> findByProductEntityNameContaining(String name);
}
