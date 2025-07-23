package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestDTO {
    private Long staffId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String reason;
    private String leaveType;
    private Long substituteStaffId;
    private String emergencyContact;
}
