package org.project.service.impl;

import lombok.RequiredArgsConstructor;

import org.project.entity.StaffEntity;
import org.project.entity.StaffShiftEntity;
import org.project.enums.StaffShiftSlot;
import org.project.model.dto.StaffShiftViewModel;
import org.project.model.dto.StaffMonthlyScheduleView;
import org.project.model.request.AssignShiftRequest;

import org.project.repository.HospitalRepository;
import org.project.repository.StaffRepository;
import org.project.repository.StaffShiftRepository;
import org.project.service.StaffShiftService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffShiftServiceImpl implements StaffShiftService {

    private final StaffRepository staffRepository;
    private final StaffShiftRepository staffShiftRepository;
    private final HospitalRepository hospitalRepository;

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
            StaffShiftViewModel vm = createStaffShiftViewModel(staff, shiftsGrouped, todayLocal);
            result.add(vm);
        }

        return result;
    }

    @Override
    public Page<StaffShiftViewModel> getTodayShiftOverviewPaginated(Pageable pageable) {
        LocalDate todayLocal = LocalDate.now();
        return getDailyShiftOverviewPaginated(todayLocal, pageable);
    }

    @Override
    public Page<StaffShiftViewModel> getDailyShiftOverviewPaginated(LocalDate date, Pageable pageable) {
        Date targetDate = Date.valueOf(date);
        Page<StaffEntity> staffPage = staffRepository.findAll(pageable);

        List<StaffShiftEntity> dayShifts = staffShiftRepository.findByDate(targetDate);

        Map<Long, List<StaffShiftEntity>> shiftsGrouped = new HashMap<>();
        for (StaffShiftEntity shift : dayShifts) {
            shiftsGrouped.computeIfAbsent(shift.getStaffEntity().getId(), k -> new ArrayList<>()).add(shift);
        }

        List<StaffShiftViewModel> content = staffPage.getContent().stream()
                .map(staff -> createStaffShiftViewModel(staff, shiftsGrouped, date))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, staffPage.getTotalElements());
    }

    private StaffShiftViewModel createStaffShiftViewModel(StaffEntity staff,
                                                         Map<Long, List<StaffShiftEntity>> shiftsGrouped,
                                                         LocalDate todayLocal) {
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
        return vm;
    }



    @Override
    public StaffMonthlyScheduleView getMonthlySchedule(Long staffId, int monthOffset) {
        LocalDate today = LocalDate.now();

        // Bắt đầu từ ngày đầu tháng hiện tại, +offset nếu muốn lùi/tiến tháng
        LocalDate startLocal = today.withDayOfMonth(1).plusMonths(monthOffset);
        LocalDate endLocal = startLocal.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());

        Date start = Date.valueOf(startLocal);
        Date end = Date.valueOf(endLocal);

        // Lấy thông tin nhân viên
        StaffEntity staff = staffRepository.findById(staffId).orElseThrow();

        // Lấy danh sách ca làm trong tháng
        List<StaffShiftEntity> shifts = staffShiftRepository.findByStaffEntity_IdAndDateBetween(
                staffId, start, end
        );

        // Khởi tạo map cho tất cả ngày trong tháng
        Map<LocalDate, List<StaffShiftSlot>> map = new LinkedHashMap<>();
        LocalDate currentDate = startLocal;
        while (!currentDate.isAfter(endLocal)) {
            map.put(currentDate, new ArrayList<>()); // ban đầu rỗng
            currentDate = currentDate.plusDays(1);
        }

        // Đổ dữ liệu từ shift vào đúng ngày
        System.out.println("[DEBUG] Tổng số shifts tìm thấy: " + shifts.size());
        for (StaffShiftEntity shift : shifts) {
            LocalDate date = shift.getDate().toLocalDate();
            System.out.println("[DEBUG] Shift ngày: " + date + ", ca: " + shift.getShiftType());
            List<StaffShiftSlot> slots = map.get(date);
            if (slots != null) {
                slots.add(shift.getShiftType());
                System.out.println("[DEBUG] Đã thêm ca " + shift.getShiftType() + " vào ngày " + date);
            } else {
                System.out.println("[DEBUG] CẢNH BÁO: Không tìm thấy slot cho ngày " + date);
            }
        }

        // Hiển thị thông tin debug về dữ liệu từ database
        System.out.println("[DEBUG] Đã tải " + shifts.size() + " ca trực từ database cho nhân viên ID: " + staffId);
        System.out.println("[DEBUG] Khoảng thời gian: " + startLocal + " đến " + endLocal);

        if (shifts.isEmpty()) {
            System.out.println("[DEBUG] Không có ca trực nào trong database cho tháng này");
        } else {
            System.out.println("[DEBUG] Danh sách ca trực từ database:");
            for (StaffShiftEntity shift : shifts) {
                System.out.println("  - Ngày: " + shift.getDate() + ", Ca: " + shift.getShiftType());
            }
        }

        // Kiểm tra sau khi mapping
        System.out.println("[DEBUG] Kiểm tra map sau khi đổ dữ liệu:");
        int totalMappedShifts = 0;
        for (Map.Entry<LocalDate, List<StaffShiftSlot>> entry : map.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                System.out.println("  - Ngày " + entry.getKey() + ": " + entry.getValue());
                totalMappedShifts += entry.getValue().size();
            }
        }
        System.out.println("[DEBUG] Tổng ca trực sau mapping: " + totalMappedShifts);

        // Tính tổng số ca trực trong tháng này (không phải số ngày)
        int totalShiftsThisMonth = (int) map.values().stream()
                .mapToLong(List::size)
                .sum();

        // Tính tổng số ngày làm việc trong tháng này (ngày có ít nhất 1 ca)
        int totalWorkingDaysThisMonth = (int) map.values().stream()
                .mapToLong(slots -> slots.isEmpty() ? 0 : 1)
                .sum();

        // Tính tổng số ca theo từng loại
        Map<StaffShiftSlot, Integer> shiftTypeCounts = new HashMap<>();
        for (StaffShiftSlot slot : StaffShiftSlot.values()) {
            shiftTypeCounts.put(slot, 0);
        }

        for (List<StaffShiftSlot> daySlots : map.values()) {
            for (StaffShiftSlot slot : daySlots) {
                shiftTypeCounts.put(slot, shiftTypeCounts.get(slot) + 1);
            }
        }

        System.out.println("[DEBUG] Tổng số ca trực trong tháng: " + totalShiftsThisMonth);
        System.out.println("[DEBUG] Tổng số ngày làm việc trong tháng: " + totalWorkingDaysThisMonth);
        System.out.println("[DEBUG] Chi tiết ca trực theo loại: " + shiftTypeCounts);

        // Tạo ViewModel để trả về
        StaffMonthlyScheduleView view = new StaffMonthlyScheduleView();
        view.setStaffId(staffId);
        view.setStaffName(staff.getFullName());
        view.setShiftsPerDay(map);
        view.setTotalWorkingDaysThisMonth(totalWorkingDaysThisMonth);
        view.setTotalShiftsThisMonth(totalShiftsThisMonth);
        view.setShiftTypeCounts(shiftTypeCounts);
        view.setMonth(startLocal.getMonthValue());
        view.setYear(startLocal.getYear());
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

        // Note: Hospital information is accessible via shift.getStaffEntity().getHospitalEntity()
        // No need to store redundant hospital data in StaffShiftEntity

        staffShiftRepository.save(shift);
    }

    @Override
    public List<org.project.entity.HospitalEntity> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    @Override
    public List<org.project.entity.StaffEntity> getStaffByHospital(Long hospitalId) {
        return staffRepository.findByHospitalEntity_Id(hospitalId);
    }

    @Override
    public List<org.project.entity.DepartmentEntity> getDepartmentsByHospital(Long hospitalId) {
        // Lấy tất cả departments có staff thuộc hospital này
        return staffRepository.findByHospitalEntity_Id(hospitalId)
                .stream()
                .map(org.project.entity.StaffEntity::getDepartmentEntity)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<org.project.entity.StaffEntity> getStaffByDepartmentAndHospital(Long departmentId, Long hospitalId) {
        return staffRepository.findByDepartmentEntity_IdAndHospitalEntity_Id(departmentId, hospitalId);
    }

    @Override
    public Page<StaffShiftEntity> getMonthlyShiftDetails(int monthOffset, Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDate startLocal = today.withDayOfMonth(1).plusMonths(monthOffset);
        LocalDate endLocal = startLocal.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());

        Date start = Date.valueOf(startLocal);
        Date end = Date.valueOf(endLocal);

        return staffShiftRepository.findByDateBetween(start, end, pageable);
    }

    @Override
    public void ensureMonthlyShiftsGenerated(Long staffId, int monthOffset) {
        LocalDate today = LocalDate.now();
        LocalDate startLocal = today.withDayOfMonth(1).plusMonths(monthOffset);
        LocalDate endLocal = startLocal.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());

        Date start = Date.valueOf(startLocal);
        Date end = Date.valueOf(endLocal);

        List<StaffShiftEntity> existing = staffShiftRepository.findByStaffEntity_IdAndDateBetween(staffId, start, end);
        System.out.println("[DEBUG] Kiểm tra dữ liệu hiện có: " + existing.size() + " ca trực");

        if (!existing.isEmpty()) {
            System.out.println("[DEBUG] Đã có " + existing.size() + " ca trực trong database, bỏ qua việc tạo mới");
            return; // Đã có dữ liệu thì bỏ qua
        }

        StaffEntity staff = staffRepository.findById(staffId).orElseThrow();
        System.out.println("[DEBUG] Tạo ca trực cho nhân viên: " + staff.getFullName());

        List<StaffShiftEntity> shifts = new ArrayList<>();
        Random random = new Random(staffId); // Sử dụng staffId làm seed
        LocalDate date = startLocal;
        int totalShifts = 0;

        while (!date.isAfter(endLocal)) {
            // 70% chance có ca trực
            if (random.nextInt(10) > 2) {
                int numShifts = random.nextInt(2) + 1; // 1-2 ca mỗi ngày
                Set<StaffShiftSlot> slots = new HashSet<>();

                for (int i = 0; i < numShifts; i++) {
                    StaffShiftSlot randomSlot = StaffShiftSlot.values()[random.nextInt(4)];
                    slots.add(randomSlot);
                }

                for (StaffShiftSlot slot : slots) {
                    StaffShiftEntity shift = new StaffShiftEntity();
                    shift.setStaffEntity(staff);
                    shift.setDate(Date.valueOf(date));
                    shift.setShiftType(slot);
                    shifts.add(shift);
                    totalShifts++;
                }
                System.out.println("[DEBUG] Ngày " + date + ": " + slots);
            }
            date = date.plusDays(1);
        }

        System.out.println("[DEBUG] Sẽ tạo " + totalShifts + " ca trực cho tháng " + startLocal.getMonth());

        try {
            staffShiftRepository.saveAll(shifts);
            System.out.println("[SUCCESS] Đã lưu thành công " + totalShifts + " ca trực vào database");
        } catch (Exception e) {
            System.err.println("[ERROR] Không thể lưu shift data: " + e.getMessage());
            System.err.println("[INFO] Có thể cần kiểm tra cấu trúc database column 'shift_type'");
            e.printStackTrace();
            // Bỏ qua lỗi để không crash application
        }
    }
    @Override
    public Map<String, List<StaffShiftSlot>> getShiftsMap(StaffMonthlyScheduleView schedule) {
        Map<String, List<StaffShiftSlot>> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("[DEBUG] getShiftsMap - Bắt đầu chuyển đổi dữ liệu");
        System.out.println("[DEBUG] Số ngày trong schedule.getShiftsPerDay(): " + schedule.getShiftsPerDay().size());

        for (Map.Entry<LocalDate, List<StaffShiftSlot>> entry : schedule.getShiftsPerDay().entrySet()) {
            String key = entry.getKey().format(formatter); // Key dạng "2025-07-21"
            List<StaffShiftSlot> slots = new ArrayList<>(entry.getValue());
            result.put(key, slots);

            if (!slots.isEmpty()) {
                System.out.println("[DEBUG] Ngày " + key + " có " + slots.size() + " ca trực: " + slots);
            }
        }

        System.out.println("[DEBUG] Kết quả shiftsMap có " + result.size() + " ngày");
        return result;
    }



    @Override
    public void generateTestShiftsForCurrentMonth(Long staffId) {
        ensureMonthlyShiftsGenerated(staffId, 0); // 0 = current month
    }

    @Override
    public void deleteShift(Long shiftId) {
        staffShiftRepository.deleteById(shiftId);
    }
}