package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalanceResponse {
    String year;
    String totalLeave;
    String usedLeave;
    String pendingLeave;
    String leaveLeft;
}
