package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.LeaveRequestEntity;
import org.project.enums.LeaveType;
import org.project.model.dto.LeaveRequestDTO;
import org.project.model.response.LeaveRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestConverter {
    private final ModelMapperConfig modelMapperConfig;

    @Autowired
    public LeaveRequestConverter(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public LeaveRequestEntity toEntity(LeaveRequestDTO leaveRequestDTO) {
        LeaveRequestEntity leaveRequestEntity = new LeaveRequestEntity();
        leaveRequestEntity.setLeaveType(LeaveType.valueOf(leaveRequestDTO.getLeaveType()));
        leaveRequestEntity.setStartDate(leaveRequestDTO.getStartDate());
        leaveRequestEntity.setEndDate(leaveRequestDTO.getEndDate());
        leaveRequestEntity.setReason(leaveRequestDTO.getReason());
        leaveRequestEntity.setEmergencyContact(leaveRequestDTO.getEmergencyContact());
        return leaveRequestEntity;
    }

    public LeaveRequestResponse toResponse(LeaveRequestEntity leaveRequestEntity) {
        LeaveRequestResponse leaveRequestResponse = new LeaveRequestResponse();
        leaveRequestResponse.setRequestId(leaveRequestEntity.getId());
        leaveRequestResponse.setStaffName(leaveRequestEntity.getStaffEntity().getFullName());
        leaveRequestResponse.setLeaveType(leaveRequestEntity.getLeaveType().getType());
        leaveRequestResponse.setStartDate(leaveRequestEntity.getStartDate().toString());
        leaveRequestResponse.setEndDate(leaveRequestEntity.getEndDate().toString());
        leaveRequestResponse.setReason(leaveRequestEntity.getReason());
        leaveRequestResponse.setEmergencyContact(leaveRequestEntity.getEmergencyContact());
        leaveRequestResponse.setStatus(leaveRequestEntity.getStatus().name());
        leaveRequestResponse.setStaffApprovalName(
                leaveRequestEntity.getApprovedBy() != null
                        ? leaveRequestEntity.getApprovedBy().getFullName()
                        : null
        );
        leaveRequestResponse.setSubstituteStaffName(
                leaveRequestEntity.getStaffSubstitute() != null
                        ? leaveRequestEntity.getStaffSubstitute().getFullName()
                        : null
        );
        leaveRequestResponse.setCreatedAt(leaveRequestEntity.getCreatedAt().toString());

        if (leaveRequestEntity.getUpdatedAt() != null) {
            leaveRequestResponse.setUpdatedAt(leaveRequestEntity.getUpdatedAt().toString());
        } else {
            leaveRequestResponse.setUpdatedAt(null);
        }

        return leaveRequestResponse;
    }
}
