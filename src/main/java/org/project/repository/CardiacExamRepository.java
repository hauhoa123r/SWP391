package org.project.repository;

import org.project.entity.CardiacExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardiacExamRepository extends JpaRepository<CardiacExamEntity, Long> {
    List<CardiacExamEntity> findByMedicalRecordId(Long medicalRecordId);
}
