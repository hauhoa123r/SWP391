package org.project.service.impl;

import org.project.converter.StaffScheduleConverter;
import org.project.entity.StaffEntity;
import org.project.model.dto.AvailabilityRequestDTO;
import org.project.model.response.StaffSubstituteResponse;
import org.project.repository.StaffRepository;
import org.project.repository.StaffScheduleRepository;
import org.project.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService{

	private final StaffRepository staffRepository;
	private final StaffScheduleRepository scheduleRepository;
	private final StaffScheduleRepository staffScheduleRepository;

    @Override
    public List<StaffSubstituteResponse> getAllStaffSubstitutes(AvailabilityRequestDTO availability) {
        if (isStaffExist(availability.getStaffId())) {
            StaffEntity staffEntity = staffRepository.findById(availability.getStaffId()).get();
            List<StaffEntity> staffSubstitutes = staffRepository.findAvailableSubstitutesNative(
                    staffEntity.getDepartmentEntity().getId(),
                    staffEntity.getHospitalEntity().getId(),
                    availability.getStaffId(),
                    availability.getStartTime(),
                    availability.getEndTime()
            );
            return staffSubstitutes.stream()
                    .map(staffScheduleConverter::toResponseSubstitute)
                    .toList();
        }
        return List.of();
    }
	@Override
	public StaffEntity getStaffById(Long id) {
		return staffRepository.getById(id);
	}

	@Override
	public boolean hasCheckedInToday(Long staffId) {
		return staffScheduleRepository.existsByStaffIdAndAvailableDateToday(staffId);
	}

    @Override
    public boolean isStaffExist(Long staffId) {
        return staffRepository.existsById(staffId);
    }

    @Override
    public StaffEntity getManagerByStaffId(Long staffId) {
        StaffEntity staffEntity = getStaffByStaffId(staffId);
        if (staffEntity.getManager() == null) {
            throw new IllegalArgumentException("Staff with ID " + staffId + " does not have a manager.");
            }
        return staffEntity.getManager();
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
        return false;
    }

	@Override
	public List<StaffEntity> getAllStaff() {
		return staffRepository.findAll();
	}


}
