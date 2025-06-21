package org.project.repository;

import org.project.entity.HospitalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<HospitalEntity, Long> {
    Page<HospitalEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
