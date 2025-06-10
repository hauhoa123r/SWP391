package org.project.repository;

import org.project.entity.MedicalProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalProfileRepository extends JpaRepository {
    Optional<MedicalProfileEntity> findByPatientId(Long patientId);
}
