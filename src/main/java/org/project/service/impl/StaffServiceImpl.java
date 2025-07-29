package org.project.service.impl;

import org.project.config.WebConstant;
import org.project.converter.StaffScheduleConverter;
import org.project.entity.StaffEntity;
import org.project.model.dto.AvailabilityRequestDTO;
import org.project.model.response.StaffSubstituteResponse;
import org.project.repository.StaffRepository;
import org.project.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private StaffRepository staffRepository;

    private StaffScheduleConverter staffScheduleConverter;

    @Autowired
    public void setStaffRepository(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Autowired
    public void setStaffScheduleConverter(StaffScheduleConverter staffScheduleConverter) {
        this.staffScheduleConverter = staffScheduleConverter;
    }

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
    public StaffEntity getStaffByStaffId(Long staffId) {
        StaffEntity staffEntity = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff with ID " + staffId + " does not exist."));
        return staffEntity;
    }

    @Override
    public void deleteStaff(Long staffId) {
        StaffEntity staffEntity = getStaffByStaffId(staffId);
        staffEntity.setStaffStatus(WebConstant.STAFF_STATUS_INACTIVE);
        staffRepository.save(staffEntity);
    }
}
