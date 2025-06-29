package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentApprovalResponse {
    private Long appointmentId;
    private String status;
    private String date;
    private String startTime;
    private String doctorName;
    private String patientName;
    private String patientPhoneNumber;
    private String patientEmail;
    private String patientAvatarBase64;
    private List<Long> appointmentConflictIds;
}
