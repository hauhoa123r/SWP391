package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.PatientEntity;
import org.project.entity.PaymentEntity;
import org.project.enums.Gender;
import org.project.enums.PatientStatus;
import org.project.exception.sql.EntityNotFoundException;
import org.project.mapper.AdminPatientMapper;
import org.project.model.request.AdminPatientUpdateRequest;
import org.project.model.response.AdminPatientDetailResponse;
import org.project.model.response.AdminPatientResponse;
import org.project.repository.AdminPatientRepository;
import org.project.repository.AdminPaymentRepository;
import org.project.repository.UserRepository;
import org.project.service.AdminPatientService;
import org.project.specification.AdminPatientSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AdminPatientServiceImpl implements AdminPatientService {

    private final AdminPatientRepository patientRepository;
    private final AdminPatientMapper patientMapper;
    private final UserRepository userRepository;
    private final AdminPaymentRepository adminPaymentRepository; // ✅ thêm repo thanh toán

    // ==========================
    // Phần liên quan đến bệnh nhân
    // ==========================

    @Override
    public Page<AdminPatientResponse> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable).map(patientMapper::toResponse);
    }

    @Override
    public Page<AdminPatientResponse> getAllPatients(Pageable pageable, String keyword, String field) {
        Page<PatientEntity> patientPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            patientPage = patientRepository.findAll(pageable);
        } else {
            switch (field == null ? "name" : field.toLowerCase()) {
                case "email" -> patientPage = patientRepository.findByEmailContainingIgnoreCase(keyword, pageable);
                case "phone" -> patientPage = patientRepository.findByPhoneNumberContainingIgnoreCase(keyword, pageable);
                case "address" -> patientPage = patientRepository.findByAddressContainingIgnoreCase(keyword, pageable);
                default -> patientPage = patientRepository.findByFullNameContainingIgnoreCase(keyword, pageable);
            }
        }

        return patientPage.map(patientMapper::toResponse);
    }

    @Override
    public Page<AdminPatientResponse> searchPatients(Pageable pageable,
                                                     String global,
                                                     String name,
                                                     String email,
                                                     String phone,
                                                     Gender gender,
                                                     Collection<Long> idIn,
                                                     LocalDate birthFrom,
                                                     LocalDate birthTo) {

        Specification<PatientEntity> spec = null;

        if (global != null && !global.isBlank()) {
            spec = AdminPatientSpecifications.globalKeyword(global);
        }

        if (name != null && !name.isBlank()) {
            spec = spec == null
                    ? AdminPatientSpecifications.fullNameContains(name)
                    : spec.and(AdminPatientSpecifications.fullNameContains(name));
        }

        if (email != null && !email.isBlank()) {
            spec = spec == null
                    ? AdminPatientSpecifications.emailContains(email)
                    : spec.and(AdminPatientSpecifications.emailContains(email));
        }

        if (phone != null && !phone.isBlank()) {
            spec = spec == null
                    ? AdminPatientSpecifications.phoneContains(phone)
                    : spec.and(AdminPatientSpecifications.phoneContains(phone));
        }

        if (gender != null) {
            spec = spec == null
                    ? AdminPatientSpecifications.genderEquals(gender)
                    : spec.and(AdminPatientSpecifications.genderEquals(gender));
        }

        if (idIn != null && !idIn.isEmpty()) {
            spec = spec == null
                    ? AdminPatientSpecifications.idIn(idIn)
                    : spec.and(AdminPatientSpecifications.idIn(idIn));
        }

        if (birthFrom != null || birthTo != null) {
            spec = spec == null
                    ? AdminPatientSpecifications.birthdateBetween(birthFrom, birthTo)
                    : spec.and(AdminPatientSpecifications.birthdateBetween(birthFrom, birthTo));
        }

        Page<PatientEntity> page = (spec != null)
                ? patientRepository.findAll(spec, pageable)
                : patientRepository.findAll(pageable);

        return page.map(patientMapper::toResponse);
    }

    @Override
    public AdminPatientDetailResponse getPatientDetail(Long id) {
        PatientEntity patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        return patientMapper.toDetailResponse(patient);
    }

    @Override
    public PatientEntity getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }

    @Override
    public AdminPatientUpdateRequest getUpdateForm(Long id) {
        PatientEntity patient = getPatientById(id);
        return patientMapper.toUpdateRequest(patient);
    }

    @Override
    public void updatePatient(Long id, AdminPatientUpdateRequest request) {
        PatientEntity patient = getPatientById(id);
        patientMapper.updatePatientFromRequest(request, patient);
        patient.setUserEntity(userRepository.findById(request.getUserId()).orElseThrow());
        patientRepository.save(patient);
    }

    // ==========================
    // ✅ Các chức năng xử lý thanh toán
    // ==========================

    @Override
    public void confirmPayment(Long id) {
        PaymentEntity payment = getPaymentById(id);
        if ("paid".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalStateException("Thanh toán này đã được xác nhận trước đó.");
        }
        payment.setStatus("PAID");
        payment.setPaymentTime(new Timestamp(System.currentTimeMillis()));
        adminPaymentRepository.save(payment);
    }

    @Override
    public void updatePayment(Long id, BigDecimal amount, String method) {
        PaymentEntity payment = getPaymentById(id);
        if ("paid".equalsIgnoreCase(payment.getStatus()) || "canceled".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalStateException("Không thể chỉnh sửa thanh toán đã xác nhận hoặc đã huỷ.");
        }

        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            payment.setAmount(amount);
        }
        if (method != null && !method.isBlank()) {
            payment.setMethod(method);
        }

        adminPaymentRepository.save(payment);
    }

    @Override
    public void cancelPayment(Long id) {
        PaymentEntity payment = getPaymentById(id);
        if ("canceled".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalStateException("Thanh toán đã bị huỷ trước đó.");
        }
        payment.setStatus("CANCELED");
        adminPaymentRepository.save(payment);
    }

    // ==========================
    // Hàm phụ trợ riêng cho thanh toán
    // ==========================
    private PaymentEntity getPaymentById(Long id) {
        return adminPaymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
    }
    @Override
    public void softDeletePatient(Long id) {
        PatientEntity patient = getPatientById(id);
        patient.setPatientStatus(PatientStatus.INACTIVE); // dùng INACTIVE là “tạm xóa”
        patientRepository.save(patient);
    }

    @Override
    public void restorePatient(Long id) {
        PatientEntity patient = getPatientById(id);
        patient.setPatientStatus(PatientStatus.ACTIVE); // phục hồi
        patientRepository.save(patient);
    }

    @Override
    public void deletePermanently(Long id) {
        patientRepository.deleteById(id); // xóa vĩnh viễn
    }

    @Override
    public Page<AdminPatientResponse> getDeletedPatients(Pageable pageable) {
        return patientRepository.findByPatientStatus(PatientStatus.INACTIVE, pageable)
                .map(patientMapper::toResponse);
    }

}
