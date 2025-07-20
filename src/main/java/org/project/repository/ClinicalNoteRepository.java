package org.project.repository;

import org.project.entity.ClinicalNoteEntity;
import org.project.entity.NeurologicExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicalNoteRepository extends JpaRepository<ClinicalNoteEntity, Long> {
    List<ClinicalNoteEntity> findByMedicalRecordId(Long medicalRecordId);

}
