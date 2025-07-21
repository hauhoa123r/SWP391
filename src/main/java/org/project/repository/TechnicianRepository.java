package org.project.repository;

import org.project.entity.TechnicianEntity;
import org.project.enums.TechnicianRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicianRepository extends JpaRepository<TechnicianEntity, Long> {
    List<TechnicianEntity> findBytechnicianRank(TechnicianRank technicianRank);
}
