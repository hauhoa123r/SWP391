package org.project.repository;

import org.project.enums.StaffRole;

import org.project.entity.StaffEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long>, JpaSpecificationExecutor<StaffEntity>, StaffRepositoryCustom {
    Page<StaffEntity> findAllByStaffRole(StaffRole staffRole, Pageable pageable);

    Page<StaffEntity> findAllByStaffRoleAndDepartmentEntityName(StaffRole staffRole, String departmentEntityName, Pageable pageable);

    List<StaffEntity> findAllByStaffRoleAndDepartmentEntityNameAndIdIsNot(StaffRole staffRole, String departmentEntityName, Long id);

    StaffEntity findByStaffRoleAndId(StaffRole staffRole, Long id);

    List<StaffEntity> findAllByStaffRole(StaffRole staffRole);

    boolean existsByStaffRoleAndId(StaffRole staffRole, Long id);

    List<StaffEntity> findAllByStaffRoleAndFullNameContainingAndDepartmentEntity_IdAndHospitalEntity_Id(StaffRole staffRole, String fullName, Long departmentId, Long hospitalId);

    // Find the first staff (lowest rank level) in the same department & hospital to act as default manager
    Optional<StaffEntity> findFirstByDepartmentEntity_IdAndHospitalEntity_IdOrderByRankLevelAsc(Long departmentId, Long hospitalId);

    Optional<StaffEntity> findFirstByDepartmentEntityIdAndStaffRole(Long departmentEntity_id, StaffRole staffRole);


}