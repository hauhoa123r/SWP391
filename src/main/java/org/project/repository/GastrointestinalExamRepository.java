package org.project.repository;

import org.project.entity.GastrointestinalExam;
import org.project.entity.NeurologicExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GastrointestinalExamRepository extends JpaRepository<GastrointestinalExam, Long> {
    List<GastrointestinalExam> findByMedicalRecordId(Long medicalRecordId);
}
