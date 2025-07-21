package org.project.repository;

import org.project.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientVRepository extends JpaRepository<PatientEntity, Long> {
}
