package org.project.repository;

import org.project.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long>{
	StaffEntity getById(Long id);
}
