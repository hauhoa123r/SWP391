package org.project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDetailDTO {
    private LocalDateTime startTime;
    private String doctorName;
    private String serviceName;
    private String departmentName;
    private String status;
}
