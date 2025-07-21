package org.project.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
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
	private final StaffScheduleRepository staffScheduleRepository;

	@Override
	public StaffEntity getStaffById(Long id) {
		return staffRepository.getById(id);
	}

	@Override
	public boolean hasCheckedInToday(Long staffId) {
		return staffScheduleRepository.existsByStaffIdAndAvailableDateToday(staffId);
	}


	@Override
	public boolean processCheckIn(Long employeeId) {
        StaffEntity staff = staffRepository.getById(employeeId);
		Calendar calendar = Calendar.getInstance();
		Timestamp startTime = new Timestamp(System.currentTimeMillis());
		calendar.setTime(startTime);
		calendar.add(Calendar.HOUR_OF_DAY, 4);
		Timestamp endTime = new Timestamp(calendar.getTimeInMillis());

        if (staff != null) { // Check for null 
            StaffScheduleEntity newRecord = new StaffScheduleEntity();
			newRecord.setAvailableDate(new Date(System.currentTimeMillis()));
            newRecord.setStaffEntity(staff);
            newRecord.setStartTime(startTime);
			newRecord.setEndTime(endTime);

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
