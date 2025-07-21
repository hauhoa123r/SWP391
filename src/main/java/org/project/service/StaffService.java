package org.project.service;

import java.util.List;

import org.project.entity.StaffEntity;
import org.springframework.stereotype.Service;
@Service
public interface StaffService {
	StaffEntity getStaffById(Long id);

	boolean hasCheckedInToday(Long employeeId);

	boolean processCheckIn(Long employeeId);

	List<StaffEntity> getAllStaff();
	
}
