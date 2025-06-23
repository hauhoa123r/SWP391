package org.project.service;

import org.project.model.response.ServiceResponse;
import org.springframework.data.domain.Page;

public interface ServiceService {
    Page<ServiceResponse> getServices(int index, int size);

    Page<ServiceResponse> getServicesByDepartment(Long departmentId, int index, int size);

    Page<ServiceResponse> searchServicesByDepartmentAndKeyword(Long departmentId, String keyword, int index, int size);

    boolean isServiceExist(Long id);

    ServiceResponse getService(Long id);
}
