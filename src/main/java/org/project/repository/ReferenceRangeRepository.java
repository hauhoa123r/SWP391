package org.project.repository;

import org.project.entity.ReferenceRangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReferenceRangeRepository extends JpaRepository<ReferenceRangeEntity, Long> {

    @Query("select r from ReferenceRangeEntity r where r.testItemEntity.id = :testTypeId\n " +
            "and r.ageMax >= :age\n" +
            "and r.ageMin <= :age")
    ReferenceRangeEntity findSymtomPatientFromUnit(@Param("testTypeId") Long testItemId,
                                                   @Param("age") Long age);
}
