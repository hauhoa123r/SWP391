package org.project.model.dto;

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
    private String sortFieldName;
    private SortDirection sortDirection;
}
