package org.project.service;

import org.project.model.dto.DoctorDTO;
import org.project.model.response.DoctorResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DoctorService {
    Page<DoctorResponse> getAll(int index, int size);

    Page<DoctorResponse> getDoctors(DoctorDTO doctorDTO, int index, int size);

    Page<DoctorResponse> getAllByDepartment(Long departmentId, int index, int size);

    Page<DoctorResponse> getAllByHospitalAndDepartment(Long hospitalId, Long departmentId, int index, int size);

    Page<DoctorResponse> searchAllByHospitalAndDepartmentAndKeyword(Long hospitalId, Long departmentId, String keyword, int index, int size);

    List<DoctorResponse> getColleagueDoctorsByDepartment(Long departmentId, Long doctorId);

    DoctorResponse getDoctor(Long doctorId);

    List<DoctorResponse> getAllByCriteria(DoctorDTO doctorDTO, int index, int size);

    List<DoctorResponse> getTop6DoctorsByDepartment(Long departmentId);

    List<DoctorResponse> getTop6DoctorsByHospital(Long hospitalId);

    DoctorResponse getTop1Doctor();

    List<DoctorResponse> getTop6Doctors();
}
