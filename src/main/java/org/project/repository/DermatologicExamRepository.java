package org.project.repository;

import org.project.entity.DermatologicExamEntity;
import org.project.entity.NeurologicExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DermatologicExamRepository extends JpaRepository<DermatologicExamEntity,Long> {
    List<DermatologicExamEntity> findByMedicalRecordId(Long medicalRecordId);

}
