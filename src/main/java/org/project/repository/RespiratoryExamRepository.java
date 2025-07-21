package org.project.repository;

import org.project.entity.RespiratoryExamEntity;
import org.project.entity.VitalSignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespiratoryExamRepository extends JpaRepository<RespiratoryExamEntity, Long> {
    List<RespiratoryExamEntity> findByMedicalRecordId(Long medicalRecordEntityId);
}
