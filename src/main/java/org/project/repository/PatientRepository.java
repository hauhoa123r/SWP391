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


    List<PatientEntity> findByUserEntity_Id(Long userEntityId);

    PatientEntity findByUserEntity_IdAndUserEntity_UserRoleAndFamilyRelationship(Long userEntityId, UserRole userEntityUserRole, FamilyRelationship familyRelationship);
}
