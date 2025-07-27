package org.project.repository;

import org.project.entity.PrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionVRepository extends JpaRepository<PrescriptionEntity, Long> {
}
