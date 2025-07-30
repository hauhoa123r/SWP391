package org.project.repository;

import org.project.entity.NeurologicExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NeurologicRepository extends JpaRepository<NeurologicExamEntity, Long> {
}
