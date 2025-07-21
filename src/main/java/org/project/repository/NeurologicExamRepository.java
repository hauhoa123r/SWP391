package org.project.repository;

import org.project.entity.CardiacExamEntity;
import org.project.entity.NeurologicExamEntity;
import org.project.model.response.NeurologicResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeurologicExamRepository extends JpaRepository<NeurologicExamEntity,Long> {
    List<NeurologicExamEntity> findByMedicalRecordId(Long medicalRecordId);
}
