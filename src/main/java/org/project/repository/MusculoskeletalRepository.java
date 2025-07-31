package org.project.repository;

import org.project.entity.MusculoskeletalExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusculoskeletalRepository extends JpaRepository<MusculoskeletalExamEntity, Long> {
}
