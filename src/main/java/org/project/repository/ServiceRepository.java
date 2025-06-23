package org.project.repository;

import org.project.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    boolean existsByIdAndDepartmentEntityStaffEntitiesId(Long id, Long departmentEntityStaffEntitiesId);
}
