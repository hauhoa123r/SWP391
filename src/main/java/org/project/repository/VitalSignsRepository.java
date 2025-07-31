package org.project.repository;

import org.project.entity.VitalSignEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VitalSignsRepository extends JpaRepository<VitalSignEntity, Long> {
}
