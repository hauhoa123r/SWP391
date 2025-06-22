package org.project.repository;

import org.project.entity.PatientEntity;
import org.project.enums.FamilyRelationship;
import org.project.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long>{

    List<PatientEntity> findAllByUserEntity_Id(Long userId);

    Page<PatientEntity> findAllByUserEntity_Id(Long userId, Pageable pageable);

    Long findFirstByUserEntity_IdOrderByIdDesc(Long userId);

    @Query("SELECT pe.relationship "
            + "FROM PatientEntity pe "
            + "WHERE pe.userEntity.id = :userId")
    List<FamilyRelationship> getAllRelationships(@Param("userId") Long userId);

    PatientEntity findByUserEntity_IdAndUserEntity_UserRoleAndRelationship(
            Long userEntityId,
            UserRole userRole,
            FamilyRelationship relationship
    );

    PatientEntity findByUserEntity_IdAndFullName(Long userId, String patientName);
}
