package org.project.repository;

import org.project.entity.PatientEntity;
import org.project.enums.FamilyRelationship;
import org.project.enums.PatientStatus;
import org.project.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<PatientEntity, Long>, JpaSpecificationExecutor<PatientEntity> {

    List<PatientEntity> findAllByUserEntity_Id(Long userId);

    Page<PatientEntity> findAllByUserEntityIdAndPatientStatus(Long userEntityId, Pageable pageable, PatientStatus patientStatus);

    Page<PatientEntity> findAllByUserEntity_Id(Long userId, Pageable pageable);

    Long findFirstByUserEntity_IdOrderByIdDesc(Long userId);
    

    Page<PatientEntity> findAllByUserEntityIdAndFullNameContainingIgnoreCase(Long userEntityId, String fullName, Pageable pageable);

    boolean existsByIdAndUserEntityId(Long id, Long userEntityId);

    @Query("SELECT pe.familyRelationship "
            + "FROM PatientEntity pe "
            + "WHERE pe.userEntity.id = :userId")
    List<FamilyRelationship> getAllRelationships(@Param("userId") Long userId);

    PatientEntity findByUserEntity_IdAndUserEntity_UserRoleAndFamilyRelationship(
            Long userEntityId,
            UserRole userRole,
            FamilyRelationship familyRelationship
    );

    @Query(value = "SELECT COUNT(DISTINCT p.patient_id) " +
            "FROM patients p " +
            "JOIN appointments a ON a.patient_id = p.patient_id " +
            "JOIN staffs s ON s.staff_id = a.doctor_id " +
            "JOIN hospitals h ON h.hospital_id = s.hospital_id " +
            "WHERE h.name = :hospitalName", nativeQuery = true)
    long countPatientByHospital(@Param("hospitalName") String hospitalName);

    PatientEntity findByUserEntity_IdAndFullName(Long userId, String patientName);

    @Query("SELECT COUNT(pe.id) FROM PatientEntity pe")
    int countAllPatients();

    @Query("FROM PatientEntity pe ORDER BY RAND() LIMIT 1")
    PatientEntity getRandom();



    PatientEntity findByUserEntity_Id(Long userEntityId);

    PatientEntity findByUserEntity_IdAndFamilyRelationship(Long id, FamilyRelationship relationship);
}
