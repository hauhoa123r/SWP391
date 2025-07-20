package org.project.repository;

import org.project.entity.GastrointestinalExam;
import org.project.entity.GenitourinaryExam;
import org.project.entity.NeurologicExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenitourinaryExamRepository extends JpaRepository<GenitourinaryExam, Long> {
    List<GenitourinaryExam> findByMedicalRecordId(Long medicalRecordId);

}
