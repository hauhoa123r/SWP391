package org.project.repository;

import org.project.entity.LeaveBalanceEntity;
import org.project.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Year;
import java.util.List;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalanceEntity, Long> {

    List<LeaveBalanceEntity> findAllByStaffEntity_IdAndYear(Long staffId, Year year);

    LeaveBalanceEntity findByStaffEntity_IdAndYearAndLeaveType(Long staffId, Year year, LeaveType leaveType);
}
