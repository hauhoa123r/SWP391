package org.project.repository;

import org.project.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<StaffEntity, Long>{

	
}
