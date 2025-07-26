package org.project.admin.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.project.admin.validation.ValidTimeRange;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ValidTimeRange(message = "Thời gian làm việc không hợp lệ")
public class StaffScheduleRequest {

    @NotNull(message = "Staff ID không được để trống")
    private Long staffId;

    @NotNull(message = "Ngày làm việc không được để trống")
    @FutureOrPresent(message = "Ngày làm việc phải là hôm nay hoặc trong tương lai")
    private LocalDate availableDate;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalDateTime startTime;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalDateTime endTime;
}
