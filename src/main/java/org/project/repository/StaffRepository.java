package org.project.repository;

import org.project.entity.StaffEntity;
import org.project.enums.StaffRole;
import org.project.enums.StaffStatus;
import org.project.enums.TechnicianRank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long>, JpaSpecificationExecutor<StaffEntity>, StaffRepositoryCustom {
    StaffEntity findByStaffRoleAndId(StaffRole staffRole, Long id);

    List<StaffEntity> findAllByStaffRole(StaffRole staffRole);

    List<StaffEntity> findByStaffRoleAndHospitalEntity_IdAndTechnicianEntity_TechnicianRank(StaffRole staffRole, Long hospitalEntityId, TechnicianRank technicianEntityTechnicianRank);

    // Find the first staff (lowest rank level) in the same department & hospital to act as default manager
    Optional<StaffEntity> findFirstByDepartmentEntity_IdAndHospitalEntity_IdOrderByRankLevelAsc(Long departmentId, Long hospitalId);

    // Tìm kiếm staff theo keyword
    Page<StaffEntity> findByFullNameContainingIgnoreCaseOrUserEntityEmailContainingIgnoreCaseOrUserEntityPhoneNumberContainingIgnoreCase(String keyword, String keyword2, String keyword3, Pageable pageable);

    // Lấy nhân viên theo bệnh viện
    List<StaffEntity> findByHospitalEntity_Id(Long hospitalId);

    // Lấy nhân viên theo phòng ban và bệnh viện
    List<StaffEntity> findByDepartmentEntity_IdAndHospitalEntity_Id(Long departmentId, Long hospitalId);

    // Soft delete functionality
    Page<StaffEntity> findByStaffStatus(StaffStatus staffStatus, Pageable pageable);

    @Query(value = """
                SELECT s.*
                FROM staffs s
                WHERE s.department_id = :deptId
                  AND s.hospital_id   = :hospitalId
                  AND s.staff_id     <> :excludedStaffId
                  AND s.staff_role   = :role
                  AND s.staff_status = 'ACTIVE'
                  AND NOT EXISTS (
                    SELECT 1
                    FROM appointments a
                    WHERE a.doctor_id = s.staff_id
                      AND a.start_time < :endTime
                      AND DATE_ADD(a.start_time, INTERVAL a.duration_minutes MINUTE) > :startTime
                  )
            """, nativeQuery = true)
    List<StaffEntity> findAvailableSubstitutesNative(
            @Param("deptId") Long deptId,
            @Param("hospitalId") Long hospitalId,
            @Param("excludedStaffId") Long excludedStaffId,
            @Param("role") String role,
            @Param("startTime") Timestamp startTime,
            @Param("endTime") Timestamp endTime
    );

    boolean existsByUserEntityEmailAndStaffStatus(String userEntityEmail, StaffStatus staffStatus);

    boolean existsByUserEntityPhoneNumberAndStaffStatus(String userEntityPhoneNumber, StaffStatus staffStatus);

    StaffEntity findByIdAndStaffStatus(Long id, StaffStatus staffStatus);

    boolean existsByUserEntityEmailAndIdNotAndStaffStatus(String userEntityEmail, Long id, StaffStatus staffStatus);

    boolean existsByUserEntityPhoneNumberAndIdNotAndStaffStatus(String userEntityPhoneNumber, Long id, StaffStatus staffStatus);

    StaffEntity findByHospitalEntityIdAndDepartmentEntityIdAndManager(Long hospitalEntityId, Long departmentEntityId, StaffEntity manager);

    @Modifying
    @Transactional
    @Query(value = """
                    UPDATE StaffEntity  se
                    SET se.manager = :staffEntity
                    WHERE se.hospitalEntity.id = :hospitalEntityId
                    AND se.departmentEntity.id = :departmentEntityId
                    AND se <> :staffEntity
            """)
    void upgradeStaffToManager(StaffEntity staffEntity, Long hospitalEntityId, Long departmentEntityId);

    boolean existsByIdAndStaffStatus(Long id, StaffStatus staffStatus);
}

