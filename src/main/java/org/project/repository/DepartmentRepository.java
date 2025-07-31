package org.project.repository;

import org.project.entity.DepartmentEntity;
import org.project.enums.DepartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long>, JpaSpecificationExecutor<DepartmentEntity> {
    boolean existsByName(String name);

    DepartmentEntity findByNameContaining(String name);

    boolean existsByIdAndStaffEntitiesId(Long departmentId, Long staffId);

    List<DepartmentEntity> findAllByDepartmentStatus(DepartmentStatus departmentStatus);

    boolean existsByNameAndDepartmentStatus(String name, DepartmentStatus departmentStatus);

    DepartmentEntity findByIdAndDepartmentStatus(Long id, DepartmentStatus departmentStatus);

    boolean existsByIdAndDepartmentStatus(Long id, DepartmentStatus departmentStatus);
}
