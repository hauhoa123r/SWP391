package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestStatisticResponse {
    private Long totalLeaveRequests;
    private Long approvedLeaveRequests;
    private Long pendingLeaveRequests;
    private Long rejectedLeaveRequests;
}
