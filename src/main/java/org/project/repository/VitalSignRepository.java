package org.project.repository;

import org.project.entity.VitalSignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSignEntity, Long> {
    List<VitalSignEntity> findByMedicalRecordId(Long medicalRecordEntityId);
}
