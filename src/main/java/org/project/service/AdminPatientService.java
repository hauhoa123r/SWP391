package org.project.service;

import org.project.entity.PatientEntity;
import org.project.enums.Gender;
import org.project.model.request.AdminPatientUpdateRequest;
import org.project.model.response.AdminPatientDetailResponse;
import org.project.model.response.AdminPatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

public interface AdminPatientService {
   Page<AdminPatientResponse> getAllPatients(Pageable pageable);
   Page<AdminPatientResponse> getAllPatients(Pageable pageable, String keyword, String field);

   Page<AdminPatientResponse> searchPatients(Pageable pageable,
                                             String global,
                                             String name,
                                             String email,
                                             String phone,
                                             Gender gender,
                                             Collection<Long> idIn,
                                             LocalDate birthFrom,
                                             LocalDate birthTo);

   AdminPatientDetailResponse getPatientDetail(Long id);
   PatientEntity getPatientById(Long id);
   AdminPatientUpdateRequest getUpdateForm(Long id);
   void updatePatient(Long id, AdminPatientUpdateRequest request);

   void confirmPayment(Long id);

   void updatePayment(Long id, BigDecimal amount, String method);

   void cancelPayment(Long id);
   void softDeletePatient(Long id);
   void restorePatient(Long id);
   void deletePermanently(Long id);

   Page<AdminPatientResponse> getDeletedPatients(Pageable pageable);

}
