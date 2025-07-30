package org.project.repository;

import org.project.entity.MedicalRecordEntity;
import org.project.entity.MedicalRecordSymptomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordSymptomRepository extends JpaRepository<MedicalRecordSymptomEntity, Long> {
    MedicalRecordSymptomEntity findByMedicalRecord_Id(Long id);

    Optional<Object> findByMedicalRecord(MedicalRecordEntity medicalRecord);
}
