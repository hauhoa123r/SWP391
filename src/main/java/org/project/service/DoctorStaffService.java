package org.project.service;

import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;

import java.util.List;

public interface DoctorStaffService {

    // Tạo mới nhân viên hoặc bác sĩ
    DoctorStaffResponse createDoctorStaff(DoctorStaffRequest request);

    // Lấy danh sách tất cả nhân viên/bác sĩ
    List<DoctorStaffResponse> getAllDoctorStaff();
}
