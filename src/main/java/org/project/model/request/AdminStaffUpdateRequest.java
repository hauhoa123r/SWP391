package org.project.model.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminStaffUpdateRequest {
    @NotBlank
    private String fullName;

    @NotNull
    private Long departmentId;

    @NotNull
    private Long hospitalId;

    private Long managerId;

    @NotNull
    private Integer rankLevel;

    private String avatarUrl;

    @NotNull
    private String staffRole;

    @NotNull
    private String staffType;
}

