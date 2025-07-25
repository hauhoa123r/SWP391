package org.project.repository;

import org.project.entity.MedicineEntity;
import org.project.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineVRepository extends JpaRepository<MedicineEntity, Long> {
}
