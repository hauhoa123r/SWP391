package org.project.service;

import org.project.entity.StaffEntity;
import org.project.model.dto.AvailabilityRequestDTO;
import org.project.model.response.StaffSubstituteResponse;

import java.util.List;

public interface StaffService {
    List<StaffSubstituteResponse> getAllStaffSubstitutes(AvailabilityRequestDTO availability);

    boolean isStaffExist(Long staffId);

    StaffEntity getManagerByStaffId(Long staffId);

    StaffEntity getStaffByStaffId(Long staffId);
}
