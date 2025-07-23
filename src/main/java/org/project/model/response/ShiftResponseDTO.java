package org.project.model.response;

import lombok.Data;
import org.project.enums.StaffShiftSlot;

import java.time.LocalDate;

@Data
public class ShiftResponseDTO {
    private Long shiftId;
    private Long staffId;
    private String staffName;
    private LocalDate date;
    private StaffShiftSlot shiftSlot;
    private String message; // Ví dụ: "Thêm thành công"
}
