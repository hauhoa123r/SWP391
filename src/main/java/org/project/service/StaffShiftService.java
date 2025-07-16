package org.project.service;

import org.project.entity.StaffShiftEntity;
import org.project.enums.StaffShiftSlot;
import org.project.model.dto.StaffShiftViewModel;
import org.project.model.dto.StaffWeeklyScheduleView;
import org.project.model.request.AssignShiftRequest;

import java.util.List;

public interface StaffShiftService {
    List<StaffShiftViewModel> getTodayShiftOverview();

    StaffWeeklyScheduleView getWeeklySchedule(Long staffId, int weekOffset);

    void assignShift(AssignShiftRequest request);

    void deleteShift(Long shiftId);
}
