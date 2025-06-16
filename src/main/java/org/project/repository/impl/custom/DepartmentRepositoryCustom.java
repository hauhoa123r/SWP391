package org.project.repository.impl.custom;

import org.project.entity.DepartmentEntity;
import org.project.enums.StaffRole;

import java.util.List;

public interface DepartmentRepositoryCustom {
    List<DepartmentEntity> findAllDepartmentsByStaffRole(StaffRole staffRole);
}
