package org.project.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.project.entity.StaffEntity;
import org.project.entity.StaffScheduleEntity;
import org.project.repository.StaffRepository;
import org.project.repository.StaffScheduleRepository;
import org.project.service.StaffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService{

	private final StaffRepository staffRepository;
	private final StaffScheduleRepository scheduleRepository;
	
	@Override
	public StaffEntity getStaffById(Long id) {
		return staffRepository.getById(id);
	}
	
	public boolean processCheckIn(Long employeeId) {
        StaffEntity staff = staffRepository.getById(employeeId);

        if (staff != null) { // Check for null 
            StaffScheduleEntity newRecord = new StaffScheduleEntity();
            newRecord.setStaffEntity(staff);
            newRecord.setStartTime(new Timestamp(System.currentTimeMillis()));

            scheduleRepository.save(newRecord);
            return true; // Check-in successful
        }
        return false; // Staff not found
    }

	@Override
	public List<StaffEntity> getAllStaff() {
		return staffRepository.findAll();
	}


}
