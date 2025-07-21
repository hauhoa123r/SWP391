package org.project.repository;

import org.project.entity.LeaveBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Year;
import java.util.List;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalanceEntity, Long> {

    List<LeaveBalanceEntity> findAllByStaffEntity_IdAndYear(Long staffId, Year year);
}
