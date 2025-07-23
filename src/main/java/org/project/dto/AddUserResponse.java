package org.project.dto;

import lombok.Builder;
import lombok.Data;
import org.project.enums.UserRole;

@Data
@Builder
public class AddUserResponse {
    private Long userId;
    private UserRole role;
    private Long patientId; // nullable
    private Long staffId;   // nullable
}
