package org.project.converter;

import org.project.entity.LeaveBalanceEntity;
import org.project.model.response.LeaveBalanceResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Component
public class LeaveBalanceConverter {

    public LeaveBalanceResponse toResponse(List<LeaveBalanceEntity> leaveBalanceEntities) {
        LeaveBalanceResponse leaveBalanceResponse = new LeaveBalanceResponse();
        if(leaveBalanceEntities == null || leaveBalanceEntities.isEmpty()) {
            leaveBalanceResponse.setYear("N/A");
            leaveBalanceResponse.setTotalLeave("0");
            leaveBalanceResponse.setUsedLeave("0");
            leaveBalanceResponse.setPendingLeave("0");
            leaveBalanceResponse.setLeaveLeft("0");
            return leaveBalanceResponse;
        }

        BigDecimal totalLeave = BigDecimal.ZERO;
        BigDecimal usedLeave = BigDecimal.ZERO;
        BigDecimal pendingLeave = BigDecimal.ZERO;

        Year year = leaveBalanceEntities.get(0).getYear();

        for (LeaveBalanceEntity ent : leaveBalanceEntities) {
            if (ent.getTotalEntitlement() != null) {
                totalLeave = totalLeave.add(ent.getTotalEntitlement());
            }
            if (ent.getUsedBalance() != null) {
                usedLeave = usedLeave.add(ent.getUsedBalance());
            }
            if (ent.getPendingBalance() != null) {
                pendingLeave = pendingLeave.add(ent.getPendingBalance());
            }
        }

        BigDecimal leaveLeft = totalLeave.subtract(usedLeave);

        leaveBalanceResponse.setYear(year.toString());
        leaveBalanceResponse.setTotalLeave(totalLeave.toString());
        leaveBalanceResponse.setUsedLeave(usedLeave.toString());
        leaveBalanceResponse.setPendingLeave(pendingLeave.toString());
        leaveBalanceResponse.setLeaveLeft(leaveLeft.toString());

        return leaveBalanceResponse;
    }
}
