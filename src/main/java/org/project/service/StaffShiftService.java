package org.project.service;

import org.project.dto.AssignShiftPageData;
import org.project.entity.StaffShiftEntity;
import org.project.enums.StaffShiftSlot;
import org.project.model.dto.StaffShiftViewModel;
import org.project.model.dto.StaffMonthlyScheduleView;
import org.project.model.request.AssignShiftRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StaffShiftService {

    // Tổng quan ca trực hôm nay
    List<StaffShiftViewModel> getTodayShiftOverview();

    Page<StaffShiftViewModel> getTodayShiftOverviewPaginated(Pageable pageable);

    // Tổng quan ca trực một ngày bất kỳ (có phân trang)
    Page<StaffShiftViewModel> getDailyShiftOverviewPaginated(LocalDate date, Pageable pageable);

    // Lịch tháng của một nhân viên
    StaffMonthlyScheduleView getMonthlySchedule(Long staffId, int monthOffset);

    // Trả về shifts theo dạng Map<String, List<ShiftSlot>> để dùng trong HTML
    Map<String, List<StaffShiftSlot>> getShiftsMap(StaffMonthlyScheduleView schedule);

    // Gán ca trực mới
    void assignShift(AssignShiftRequest request);

    // Chi tiết ca trực trong tháng (admin dùng để xem toàn bộ)
    Page<StaffShiftEntity> getMonthlyShiftDetails(int monthOffset, Pageable pageable);

    // Tạo dữ liệu mẫu nếu chưa có
    void ensureMonthlyShiftsGenerated(Long staffId, int monthOffset);

    // Thêm dữ liệu test cho tháng hiện tại
    void generateTestShiftsForCurrentMonth(Long staffId);

   
    AssignShiftPageData prepareAssignShiftPage(String date, Long hospitalId, Long departmentId);


    // Xoá ca trực
    void deleteShift(Long shiftId);

    // Tìm kiếm ca trực với AND/OR logic
    Page<StaffShiftViewModel> searchStaffShifts(String staffName, String dateFrom, String dateTo, 
                                               List<StaffShiftSlot> shiftTypes, String hospitalName, 
                                               String departmentName, Long hospitalId, Long departmentId, 
                                               Long staffId, String searchOperation, Pageable pageable);

    // Lấy tất cả bệnh viện
    List<org.project.entity.HospitalEntity> getAllHospitals();

    // Lấy nhân viên theo bệnh viện
    List<org.project.entity.StaffEntity> getStaffByHospital(Long hospitalId);

    // Lấy phòng ban theo bệnh viện (thông qua staff)
    List<org.project.entity.DepartmentEntity> getDepartmentsByHospital(Long hospitalId);

    // Lấy nhân viên theo phòng ban và bệnh viện
    List<org.project.entity.StaffEntity> getStaffByDepartmentAndHospital(Long departmentId, Long hospitalId);
}
