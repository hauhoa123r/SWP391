package org.project.model.request;

import lombok.Data;
import org.project.enums.StaffShiftSlot;

import java.time.LocalDate;

@Data
public class AssignShiftRequest {
    private Long staffId;
    private LocalDate date;
    private StaffShiftSlot shiftSlot;
}
