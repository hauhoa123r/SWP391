package org.project.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.operation.SortDirection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private String staffEntityFullName;
    private Long staffEntityDepartmentEntityId;
    private Long minStarRating;
    @NotBlank
    private String sortFieldName;
    @NotNull
    private SortDirection sortDirection;
}
