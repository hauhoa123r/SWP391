package org.project.repository;

import org.project.entity.Batch;
import org.project.entity.MedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BatchesRepository extends JpaRepository<Batch, Long> {
    Set<Batch> findByMedicine(MedicineEntity medicine);
}
