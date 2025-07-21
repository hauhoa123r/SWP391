package org.project.repository;

import org.project.entity.MedicalRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {
    MedicalRecordEntity findMedicalRecordEntityByAppointmentEntityId(Long id);

public interface MedicalRecordRepository extends JpaRepository <MedicalRecordEntity, Long>{
    List<MedicalRecordEntity> findByPatientEntity_Id(Long patientId);
}