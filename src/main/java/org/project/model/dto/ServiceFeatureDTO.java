package org.project.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFeatureDTO {
    private Long id;
    @NotBlank(message = "Tên tính năng không được để trống")
    private String name;
    @NotBlank(message = "Mô tả tính năng không được để trống")
    private String description;
}
