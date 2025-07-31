package org.project.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.operation.SortDirection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    @NotBlank(message = "Tên khoa không được để trống")
    private String name;
    private String description;
    private String videoUrl;
    private String bannerUrl;
    private String slogan;
    private String sortFieldName;
    private SortDirection sortDirection;
}
