package org.project.admin.dto.response;

import lombok.Data;
import org.project.admin.enums.doctors.DoctorRank;
@Data
public class DoctorResponse {
    private Long doctorId;
    private DoctorRank doctorRank;
    private String fullName;
    private String avatarUrl;
}
