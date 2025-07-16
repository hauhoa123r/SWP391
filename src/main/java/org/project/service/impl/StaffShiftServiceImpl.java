package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.StaffEntity;
import org.project.entity.StaffShiftEntity;
import org.project.enums.StaffShiftSlot;
import org.project.model.dto.StaffShiftViewModel;
import org.project.model.dto.StaffWeeklyScheduleView;
import org.project.model.request.AssignShiftRequest;
import org.project.repository.StaffRepository;
import org.project.repository.StaffShiftRepository;
import org.project.service.StaffShiftService;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StaffShiftServiceImpl implements StaffShiftService {

    private final StaffRepository staffRepository;
    private final StaffShiftRepository staffShiftRepository;

    @Override
    public List<StaffShiftViewModel> getTodayShiftOverview() {
        LocalDate todayLocal = LocalDate.now();
        Date today = Date.valueOf(todayLocal);
        List<StaffEntity> staffList = staffRepository.findAll();

        List<StaffShiftEntity> todayShifts = staffShiftRepository.findByDate(today);

        Map<Long, List<StaffShiftEntity>> shiftsGrouped = new HashMap<>();
        for (StaffShiftEntity shift : todayShifts) {
            shiftsGrouped.computeIfAbsent(shift.getStaffEntity().getId(), k -> new ArrayList<>()).add(shift);
        }

        List<StaffShiftViewModel> result = new ArrayList<>();
        for (StaffEntity staff : staffList) {
            StaffShiftViewModel vm = new StaffShiftViewModel();
            vm.setStaffId(staff.getId());
            vm.setFullName(staff.getFullName());
            vm.setDepartmentName(staff.getDepartmentEntity().getName());
            vm.setHospitalName(staff.getHospitalEntity().getName());

            Date monthStart = Date.valueOf(todayLocal.withDayOfMonth(1));
            Date monthEnd = Date.valueOf(todayLocal.with(TemporalAdjusters.lastDayOfMonth()));

            int total = staffShiftRepository.countByStaffEntity_IdAndDateBetween(
                    staff.getId(), monthStart, monthEnd
            );
            vm.setTotalWorkingDaysThisMonth(total);

            Map<StaffShiftSlot, String> slotStatus = new LinkedHashMap<>();
            for (StaffShiftSlot slot : StaffShiftSlot.values()) {
                slotStatus.put(slot, "Off");
            }
            if (shiftsGrouped.containsKey(staff.getId())) {
                for (StaffShiftEntity shift : shiftsGrouped.get(staff.getId())) {
                    slotStatus.put(shift.getShiftType(), "Working");
                }
            }
            vm.setStatusPerSlotToday(slotStatus);
            result.add(vm);
        }

        return result;
    }

    @Override
    public StaffWeeklyScheduleView getWeeklySchedule(Long staffId, int weekOffset) {
        LocalDate today = LocalDate.now();
        LocalDate startLocal = today.with(java.time.DayOfWeek.MONDAY).plusWeeks(weekOffset);
        LocalDate endLocal = startLocal.plusDays(6);

        Date start = Date.valueOf(startLocal);
        Date end = Date.valueOf(endLocal);

        StaffEntity staff = staffRepository.findById(staffId).orElseThrow();
        List<StaffShiftEntity> shifts = staffShiftRepository.findByStaffEntity_IdAndDateBetween(staffId, start, end);

        Map<LocalDate, List<StaffShiftSlot>> map = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            map.put(startLocal.plusDays(i), new ArrayList<>());
        }
        for (StaffShiftEntity shift : shifts) {
            LocalDate dateKey = shift.getDate().toLocalDate();
            map.get(dateKey).add(shift.getShiftType());
        }

        StaffWeeklyScheduleView view = new StaffWeeklyScheduleView();
        view.setStaffId(staffId);
        view.setStaffName(staff.getFullName());
        view.setShiftsPerDay(map);
        return view;
    }

    @Override
    public void assignShift(AssignShiftRequest request) {
        Date date = Date.valueOf(request.getDate());

        boolean exists = staffShiftRepository.existsByStaffEntity_IdAndDateAndShiftType(
                request.getStaffId(), date, request.getShiftSlot()
        );
        if (exists) return;

        StaffShiftEntity shift = new StaffShiftEntity();
        shift.setStaffEntity(staffRepository.findById(request.getStaffId()).orElseThrow());
        shift.setDate(date);
        shift.setShiftType(request.getShiftSlot());
        staffShiftRepository.save(shift);
    }

    @Override
    public void deleteShift(Long shiftId) {
        staffShiftRepository.deleteById(shiftId);
    }
}
