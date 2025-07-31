package org.project.repository;

import org.project.entity.TestItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestItemRepository extends JpaRepository<TestItemEntity, Long> {
    List<TestItemEntity> findByTestTypeEntity_Id(Long testRequestId);
}
