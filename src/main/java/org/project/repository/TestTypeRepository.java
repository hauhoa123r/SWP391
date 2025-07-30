package org.project.repository;

import org.project.entity.TestTypeEntity;
import org.project.repository.impl.TestTypeRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TestTypeRepository extends JpaRepository<TestTypeEntity, Long>, TestTypeRepositoryCustom {
    Page<TestTypeEntity> findByTestTypeNameContainingIgnoreCase(String testTypeName, Pageable pageable);
    Page<TestTypeEntity> findByTestTypeNameContainingIgnoreCaseAndStatus(String name,String status, Pageable pageable);
}