package org.project.service;

import org.project.model.request.AdminPatientRequest;
import org.project.model.response.AdminPatientResponse;
import org.project.model.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminPatientService {
   List<AdminPatientResponse> getAllPatients();
   PageResponse<AdminPatientResponse> getAllPatients(Pageable pageable);
   PageResponse<AdminPatientResponse> getPatientPage(Pageable pageable);
   AdminPatientResponse getPatientById(Long id);
   PageResponse<AdminPatientResponse> searchPatients(AdminPatientRequest req, Pageable pageable);
}
