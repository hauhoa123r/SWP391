package org.project.repository;

import org.project.entity.RespiratoryExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespiratoryRepository extends JpaRepository<RespiratoryExamEntity, Long> {
}
