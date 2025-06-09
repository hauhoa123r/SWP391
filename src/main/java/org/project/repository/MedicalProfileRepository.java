package org.project.repository;

import org.project.entity.MedicalProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalProfileRepository extends JpaRepository<MedicalProfileEntity, Long> {
}
