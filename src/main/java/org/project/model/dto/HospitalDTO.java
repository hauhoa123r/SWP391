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
public class HospitalDTO {
    private String name;
    private String address;
    private String sortFieldName;
    private SortDirection sortDirection;
}
