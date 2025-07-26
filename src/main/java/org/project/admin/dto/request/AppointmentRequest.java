package org.project.admin.dto.request;

import org.project.admin.enums.appoinements.AppointmentStatus;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    @NotNull(message = "ID bác sĩ không được để trống")
    private Long doctorId;

    @NotNull(message = "ID bệnh nhân không được để trống")
    private Long patientId;

    @NotNull(message = "ID dịch vụ không được để trống")
    private Long serviceId;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @Future(message = "Thời gian bắt đầu phải trong tương lai")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian khám không được để trống")
    @Positive(message = "Thời gian khám phải lớn hơn 0 phút")
    @Min(value = 16, message = "Thời gian khám phải lớn hơn 15 phút")
    private Integer durationMinutes;

    private AppointmentStatus appointmentStatus;
    private Long schedulingCoordinatorId;
}
