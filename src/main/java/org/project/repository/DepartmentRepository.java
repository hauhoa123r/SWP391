package org.project.repository;

import org.project.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long>, JpaSpecificationExecutor<DepartmentEntity> {
    boolean existsByName(String name);

    DepartmentEntity findByNameContaining(String name);
    boolean existsByIdAndStaffEntitiesId(Long departmentId, Long staffId);
}
