package org.project.service;

import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DoctorStaffService {

    // Tạo mới nhân viên hoặc bác sĩ
    DoctorStaffResponse createDoctorStaff(DoctorStaffRequest request);

    // Lấy chi tiết nhân viên/bác sĩ theo ID
    DoctorStaffResponse getDoctorStaff(Long staffId);

    // Lấy dữ liệu form chỉnh sửa
    DoctorStaffRequest getUpdateForm(Long staffId);

    // Lấy danh sách tất cả nhân viên/bác sĩ
    List<DoctorStaffResponse> getAllDoctorStaff();

    // Tìm kiếm theo keyword ở tất cả trường
    List<DoctorStaffResponse> searchDoctorStaff(String keyword);

    // Tìm kiếm theo trường cụ thể ("fullName", "email", "phoneNumber")
    List<DoctorStaffResponse> searchDoctorStaff(String field, String keyword);

    String handleAvatarUpload(MultipartFile avatarFile) throws IOException;
}
