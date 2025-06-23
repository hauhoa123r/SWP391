package org.project.repository;

import org.project.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
}
