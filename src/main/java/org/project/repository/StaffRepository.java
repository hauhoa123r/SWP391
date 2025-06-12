package org.project.repository;

import org.project.entity.StaffEntity;
import org.project.enums.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StaffRepository extends JpaRepository<StaffEntity, Long>, JpaSpecificationExecutor<StaffEntity> {
    StaffEntity findByStaffRoleAndId(StaffRole staffRole, Long id);

    boolean existsByStaffRoleAndId(StaffRole staffRole, Long id);
}
