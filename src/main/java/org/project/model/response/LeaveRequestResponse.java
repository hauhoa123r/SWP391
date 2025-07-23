package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestResponse {
    private Long requestId;
    private String staffName;
    private String staffApprovalName;
    private String status;
    private String leaveType;
    private String totalDays;
    private String halfDayType;
    private String reason;
    private String emergencyContact;
    private String startDate;
    private String endDate;
    private String substituteStaffName;
    private String createdAt;
    private String updatedAt;
}
