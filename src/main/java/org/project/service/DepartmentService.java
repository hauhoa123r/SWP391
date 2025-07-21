package org.project.service;

import org.project.model.response.DepartmentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAll();

    Page<DepartmentResponse> getAllHaveDoctorByHospital(Long hospitalId, int pageIndex, int pageSize);

    Page<DepartmentResponse> getAllHaveDoctorByHospitalAndKeyword(Long hospitalId, String keyword, int pageIndex, int pageSize);

    List<DepartmentResponse> getAllHaveDoctor();

    boolean isDepartmentNameExist(String departmentName);
}
