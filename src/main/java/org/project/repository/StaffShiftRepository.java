package org.project.repository;

import org.project.entity.StaffShiftEntity;
import org.project.enums.StaffShiftSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface StaffShiftRepository extends JpaRepository<StaffShiftEntity, Long> {

    // Lấy các ca trực của 1 staff theo khoảng ngày
    List<StaffShiftEntity> findByStaffEntity_IdAndDateBetween(Long staffId, Date start, Date end);

    // Lấy danh sách ca trực theo ngày
    List<StaffShiftEntity> findByDate(Date date);

    // Đếm tổng số ca trực trong tháng
    int countByStaffEntity_IdAndDateBetween(Long staffId, Date start, Date end);

    // Kiểm tra đã có phân công ca trực chưa
    boolean existsByStaffEntity_IdAndDateAndShiftType(Long staffId, Date date, StaffShiftSlot shiftSlot);
}
