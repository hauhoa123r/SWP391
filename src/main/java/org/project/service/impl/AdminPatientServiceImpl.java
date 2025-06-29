package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.PatientEntity;
import org.project.mapper.AdminPatientMapper;
import org.project.model.request.AdminPatientRequest;
import org.project.model.response.AdminPatientResponse;
import org.project.model.response.PageResponse;
import org.project.repository.AdminPatientRepository;
import org.project.service.AdminPatientService;
import org.project.specification.AdminPatientSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPatientServiceImpl implements AdminPatientService {

   private final AdminPatientRepository adminPatientRepository;
   private final AdminPatientMapper adminPatientMapper;

   // Lấy tất cả bệnh nhân
   @Override
   public List<AdminPatientResponse> getAllPatients() {
       return adminPatientMapper.toResponseList(adminPatientRepository.findAll());
   }

   // Lấy tất cả bệnh nhân với phân trang
   @Override
   public PageResponse<AdminPatientResponse> getAllPatients(Pageable pageable) {
       return getPatientPage(pageable); // Tái sử dụng logic từ getPatientPage
   }

   // Lấy các bệnh nhân với phân trang
   @Override
   public PageResponse<AdminPatientResponse> getPatientPage(Pageable pageable) {
       Page<PatientEntity> page = adminPatientRepository.findAll(pageable);  // Lấy dữ liệu phân trang từ repository
       Page<AdminPatientResponse> mappedPage = page.map(adminPatientMapper::toResponse);  // Ánh xạ từ PatientEntity sang AdminPatientResponse
       return new PageResponse<>(mappedPage);  // Trả về dữ liệu phân trang
   }

   // Lấy bệnh nhân theo ID
   @Override
   public AdminPatientResponse getPatientById(Long id) {
       PatientEntity patient = adminPatientRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Patient not found"));
       return adminPatientMapper.toResponse(patient);  // Ánh xạ từ PatientEntity sang AdminPatientResponse
   }

   // Tìm kiếm bệnh nhân theo các tiêu chí với phân trang
   @Override
   public PageResponse<AdminPatientResponse> searchPatients(AdminPatientRequest req, Pageable pageable) {
       Specification<PatientEntity> spec = AdminPatientSpecification.filter(req);  // Sử dụng Specification để tìm kiếm
       Page<PatientEntity> page = adminPatientRepository.findAll(spec, pageable);  // Tìm kiếm với Specification và phân trang
       return new PageResponse<>(page.map(adminPatientMapper::toResponse));  // Trả về kết quả phân trang
   }
}
