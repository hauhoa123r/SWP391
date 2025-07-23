package org.project.dto;


import lombok.Data;
import org.project.entity.DepartmentEntity;
import org.project.entity.HospitalEntity;
import org.project.entity.StaffEntity;
import org.project.enums.StaffShiftSlot;

import java.time.LocalDate;
import java.util.List;

@Data
public class AssignShiftPageData {
    private List<HospitalEntity> allHospitals;
    private List<DepartmentEntity> allDepartments;
    private List<StaffEntity> allStaff;
    private StaffShiftSlot[] shiftSlots;
    private LocalDate selectedDate;
    private Long selectedHospitalId;
    private Long selectedDepartmentId;
}

