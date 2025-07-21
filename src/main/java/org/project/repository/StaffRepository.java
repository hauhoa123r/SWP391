package org.project.repository;

import org.project.enums.StaffRole;
<<<<<<< HEAD

import org.project.entity.StaffEntity;
=======
import org.project.enums.TechnicianRank;
import org.project.model.response.ManagerNameResponse;
>>>>>>> 1fe7b34939d6bcd04ae5de38ce13891189c4ebc0
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long>, JpaSpecificationExecutor<StaffEntity>, StaffRepositoryCustom {
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT s.staffRole FROM StaffEntity s")
    java.util.List<org.project.enums.StaffRole> findDistinctStaffRoles();
    Page<StaffEntity> findAllByStaffRole(StaffRole staffRole, Pageable pageable);

    Page<StaffEntity> findAllByStaffRoleAndDepartmentEntityName(StaffRole staffRole, String departmentEntityName, Pageable pageable);

    List<StaffEntity> findAllByStaffRoleAndDepartmentEntityNameAndIdIsNot(StaffRole staffRole, String departmentEntityName, Long id);

    StaffEntity findByStaffRoleAndId(StaffRole staffRole, Long id);

    List<StaffEntity> findAllByStaffRole(StaffRole staffRole);

    boolean existsByStaffRoleAndId(StaffRole staffRole, Long id);

    List<StaffEntity> findAllByStaffRoleAndFullNameContainingAndDepartmentEntity_IdAndHospitalEntity_Id(StaffRole staffRole, String fullName, Long departmentId, Long hospitalId);

<<<<<<< HEAD
    // Find the first staff (lowest rank level) in the same department & hospital to act as default manager
    Optional<StaffEntity> findFirstByDepartmentEntity_IdAndHospitalEntity_IdOrderByRankLevelAsc(Long departmentId, Long hospitalId);

    Optional<StaffEntity> findFirstByDepartmentEntityIdAndStaffRole(Long departmentEntity_id, StaffRole staffRole);

    // Tìm kiếm staff theo keyword
    Page<StaffEntity> findByFullNameContainingIgnoreCaseOrUserEntityEmailContainingIgnoreCaseOrUserEntityPhoneNumberContainingIgnoreCase(String keyword, String keyword2, String keyword3, Pageable pageable);

    // Lấy nhân viên theo bệnh viện
    List<StaffEntity> findByHospitalEntity_Id(Long hospitalId);

    // Lấy nhân viên theo phòng ban và bệnh viện
    List<StaffEntity> findByDepartmentEntity_IdAndHospitalEntity_Id(Long departmentId, Long hospitalId);
=======
    List<StaffEntity> findByStaffRoleAndHospitalEntity_IdAndTechnicianEntity_TechnicianRank(StaffRole staffRole, Long hospitalEntityId, TechnicianRank technicianEntityTechnicianRank);

    StaffEntity findByUserEntity_Id(Long userEntityId);
>>>>>>> 1fe7b34939d6bcd04ae5de38ce13891189c4ebc0
}

