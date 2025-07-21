package org.project.repository;

import org.project.entity.StaffEntity;
import org.project.enums.StaffRole;
import org.project.enums.TechnicianRank;
import org.project.model.response.ManagerNameResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long>{
	StaffEntity getById(Long id);

    Page<StaffEntity> findAllByStaffRole(StaffRole staffRole, Pageable pageable);

    Page<StaffEntity> findAllByStaffRoleAndDepartmentEntityName(StaffRole staffRole, String departmentEntityName, Pageable pageable);

    List<StaffEntity> findAllByStaffRoleAndDepartmentEntityNameAndIdIsNot(StaffRole staffRole, String departmentEntityName, Long id);

    StaffEntity findByStaffRoleAndId(StaffRole staffRole, Long id);

    List<StaffEntity> findAllByStaffRole(StaffRole staffRole);

    boolean existsByStaffRoleAndId(StaffRole staffRole, Long id);

    List<StaffEntity> findAllByStaffRoleAndFullNameContainingAndDepartmentEntity_IdAndHospitalEntity_Id(StaffRole staffRole, String fullName, Long departmentId, Long hospitalId);

    List<StaffEntity> findByStaffRoleAndHospitalEntity_IdAndTechnicianEntity_TechnicianRank(StaffRole staffRole, Long hospitalEntityId, TechnicianRank technicianEntityTechnicianRank);

    StaffEntity findByUserEntity_Id(Long userEntityId);

    @Query(value = """
                    SELECT s.*
                    FROM staffs s
                    WHERE s.department_id = :deptId
                        AND s.hospital_id   = :hospitalId
                        AND s.staff_id     <> :excludedStaffId
                        AND s.staff_id NOT IN (
                                            SELECT a.doctor_id
                                            FROM appointments a
                                            WHERE a.start_time < :endTime
                                            AND DATE_ADD(a.start_time, INTERVAL a.duration_minutes MINUTE) > :startTime
                                            )
                        AND s.staff_id NOT IN (
                                            SELECT sch.staff_id
                                            FROM staff_schedules sch
                                            WHERE sch.start_time < :endTime
                                            AND sch.end_time   > :startTime
    )
""", nativeQuery = true)
    List<StaffEntity> findAvailableSubstitutesNative(
            @Param("deptId")         Long deptId,
            @Param("hospitalId")     Long hospitalId,
            @Param("excludedStaffId")Long excludedStaffId,
            @Param("startTime") Timestamp startTime,
            @Param("endTime")        Timestamp endTime
    );
}
