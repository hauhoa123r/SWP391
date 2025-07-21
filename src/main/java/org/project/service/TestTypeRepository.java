package org.project.service;

import org.project.entity.TestTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestTypeRepository extends JpaRepository<TestTypeEntity, Long> {
}
