package org.project.repository;

import org.project.entity.PatientEntity;
import org.project.enums.FamilyRelationship;
import org.project.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    List<PatientEntity> findAllByUserEntity_Id(Long userId);

    PatientEntity findByUserEntity_IdAndUserEntity_UserRoleAndRelationship(
            Long userEntityId,
            UserRole userRole,
            FamilyRelationship relationship
    );

    PatientEntity findByUserEntity_IdAndFullName(Long userId, String patientName);

}
