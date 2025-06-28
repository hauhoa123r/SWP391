package org.project.repository;

import org.project.entity.MedicalProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface MedicalProfileRepository extends JpaRepository<MedicalProfileEntity, Long> {
    MedicalProfileEntity findByPatientEntityId(Long patientId);
}
