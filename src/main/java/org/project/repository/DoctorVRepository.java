package org.project.repository;

import org.project.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorVRepository extends JpaRepository<DoctorEntity,Long> {
    DoctorEntity findByStaffEntityId(Long staffEntityId);
}
