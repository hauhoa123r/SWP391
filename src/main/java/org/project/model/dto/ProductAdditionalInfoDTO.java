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
public class ProductAdditionalInfoDTO {
    private Long id;
    @NotBlank(message = "Tên thông tin bổ sung không được để trống")
    private String name;
    @NotBlank(message = "Giá trị thông tin bổ sung không được để trống")
    private String value;
}
