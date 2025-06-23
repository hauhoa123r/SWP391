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
public class ServiceResponse {
    private List<ServiceFeatureResponse> serviceFeatureEntities;
    private Long id;
    private ProductResponse productEntity;
    private DepartmentResponse departmentEntity;
}
