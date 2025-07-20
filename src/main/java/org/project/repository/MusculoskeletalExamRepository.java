package org.project.repository;

import org.project.entity.MusculoskeletalExamEntity;
import org.project.entity.NeurologicExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusculoskeletalExamRepository extends JpaRepository<MusculoskeletalExamEntity, Long> {
    List<MusculoskeletalExamEntity> findByMedicalRecordId(Long medicalRecordId);

}
