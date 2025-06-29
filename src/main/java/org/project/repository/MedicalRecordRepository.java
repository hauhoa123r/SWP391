package org.project.repository;

import org.project.entity.MedicalRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {
    MedicalRecordEntity findMedicalRecordEntityByAppointmentEntityId(Long id);
}
