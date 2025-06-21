package org.project.repository;

import org.project.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    List<PatientEntity> findAllByUserEntity_Id(Long userId);

    Page<PatientEntity> findAllByUserEntityId(Long userEntityId, Pageable pageable);

    Page<PatientEntity> findAllByUserEntityIdAndFullNameContainingIgnoreCase(Long userEntityId, String fullName, Pageable pageable);

    boolean existsByIdAndUserEntityId(Long id, Long userEntityId);
}
