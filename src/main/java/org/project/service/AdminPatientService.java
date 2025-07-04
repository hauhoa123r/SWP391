package org.project.service;

import org.project.entity.PatientEntity;
import org.project.model.request.AdminPatientUpdateRequest;
import org.project.model.response.AdminPatientDetailResponse;
import org.project.model.response.AdminPatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPatientService {
   Page<AdminPatientResponse> getAllPatients(Pageable pageable, String keyword);
   AdminPatientDetailResponse getPatientDetail(Long id);
   PatientEntity getPatientById(Long id);
   AdminPatientUpdateRequest getUpdateForm(Long id);
   void updatePatient(Long id, AdminPatientUpdateRequest request);
}
