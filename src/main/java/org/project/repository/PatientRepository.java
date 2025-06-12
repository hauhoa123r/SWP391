package org.project.repository;

import org.project.entity.PatientEntity;
import org.project.enums.FamilyRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long>{

    List<PatientEntity> findAllByUserEntity_Id(Long userId);

    Long findFirstByUserEntity_IdOrderByIdDesc(Long userId);

    @Query("SELECT pe.relationship "
            + "FROM PatientEntity pe "
            + "WHERE pe.userEntity.id = :userId")
    List<FamilyRelationship> getAllRelationships(@Param("userId") Long userId);
}
