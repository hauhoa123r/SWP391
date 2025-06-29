package org.project.repository;

import org.project.entity.TestRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRequestRepository extends JpaRepository<TestRequestEntity, Long> {
    Page<TestRequestEntity> findAll(Pageable pageable);
}
