package org.project.service;

import org.project.model.dto.ServiceDTO;
import org.project.model.response.ServiceResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ServiceService {
    Page<ServiceResponse> getServices(int index, int size, ServiceDTO serviceDTO);

    List<ServiceResponse> getTop3ServicesByHospital(Long hospitalId);

    Page<ServiceResponse> getServicesByDepartment(Long departmentId, int index, int size);

    Page<ServiceResponse> searchServicesByDepartmentAndKeyword(Long departmentId, String keyword, int index, int size);

    boolean isActiveServiceExist(Long id);

    ServiceResponse getActiveService(Long id);

    Long countActiveService();

    List<ServiceResponse> getTopServices(int top);
}
