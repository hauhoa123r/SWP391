package org.project.service;

import org.project.model.dto.DepartmentDTO;
import org.project.model.response.DepartmentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse getDepartment(Long id);

    List<DepartmentResponse> getDepartments();

    Page<DepartmentResponse> getDepartments(int pageIndex, int pageSize, DepartmentDTO departmentDTO);

    Page<DepartmentResponse> getDepartmentsHaveDoctorByHospital(Long hospitalId, int pageIndex, int pageSize);

    Page<DepartmentResponse> getDepartmentsHaveDoctorByHospitalAndKeyword(Long hospitalId, String keyword, int pageIndex, int pageSize);

    List<DepartmentResponse> getDepartmentsHaveDoctor();

    boolean isDepartmentNameExist(String departmentName);

    List<DepartmentResponse> getAll();

    void createDepartment(DepartmentDTO departmentDTO);

    void updateDepartment(Long id, DepartmentDTO departmentDTO);

    void deleteDepartment(Long id);

}
