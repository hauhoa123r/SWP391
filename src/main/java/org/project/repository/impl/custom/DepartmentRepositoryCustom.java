package org.project.repository.impl.custom;

import org.project.entity.DepartmentEntity;
import org.project.enums.StaffRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentRepositoryCustom {
    List<DepartmentEntity> findAllByStaffEntitiesStaffRole(StaffRole staffEntitiesStaffRole);

    Page<DepartmentEntity> findAllByStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(StaffRole staffEntitiesStaffRole, Long hospitalEntityId, Pageable pageable);

    Page<DepartmentEntity> findAllByNameContainingAndStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(
            String name, StaffRole staffEntitiesStaffRole, Long hospitalEntityId, Pageable pageable);
}
