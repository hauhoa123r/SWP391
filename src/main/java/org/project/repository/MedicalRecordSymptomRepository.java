package org.project.repository;

import org.project.entity.MedicalRecordSymptomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordSymptomRepository extends JpaRepository<MedicalRecordSymptomEntity, Long> {
    List<MedicalRecordSymptomEntity> findByMedicalRecordEntityId(Long medicalRecordId);
}
