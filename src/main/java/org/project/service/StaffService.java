package org.project.service;

import org.project.entity.StaffEntity;
import org.project.model.dto.AvailabilityRequestDTO;
import org.project.model.dto.StaffDTO;
import org.project.model.response.StaffResponse;
import org.project.model.response.StaffSubstituteResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StaffService {
    List<StaffSubstituteResponse> getAllStaffSubstitutes(AvailabilityRequestDTO availability);

    boolean isStaffExist(Long staffId);

    StaffEntity getManagerByStaffId(Long staffId);

    StaffEntity getStaffByStaffId(Long staffId);

    void deleteStaff(Long staffId);

    Page<StaffResponse> getStaffs(int index, int size, StaffDTO staffDTO);

    void createStaff(StaffDTO staffDTO);

    void updateStaff(Long staffId, StaffDTO staffDTO);

    void setStaffToManager(Long staffId);
}
