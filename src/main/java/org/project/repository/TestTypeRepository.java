package org.project.repository;

import org.project.entity.TestTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTypeRepository extends JpaRepository<TestTypeEntity, Long> {
    Page<TestTypeEntity> findByTestTypeNameContainingIgnoreCase(String testTypeName, Pageable pageable);
}
