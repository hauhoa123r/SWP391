package org.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.project.dto.AppointmentDetailDTO;
import org.project.entity.AppointmentEntity;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class PatientProfileController {

    private final UserRepository userRepository;

    @GetMapping("/patient/profile")
    @Transactional
    public String viewProfile(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth-view-login";
        }
        Long userId = (Long) request.getAttribute("userId");

        UserEntity user = (UserEntity) authentication.getPrincipal();

        Optional<PatientEntity> optionalPatient = user.getPatientEntities().stream().findFirst();
        if (optionalPatient.isEmpty()) {
            model.addAttribute("message", "Không tìm thấy hồ sơ bệnh nhân.");
            return "frontend/patient-profile";
        }

        PatientEntity patient = optionalPatient.get();

        List<AppointmentDetailDTO> appointmentDTOs = buildAppointmentDetails(patient.getAppointmentEntities());

        model.addAttribute("user", user);
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointmentDTOs);
        model.addAttribute("medicalRecords", patient.getMedicalRecordEntities()); // bạn có thể convert sang DTO tương tự nếu cần

        return "frontend/patient-profile";
    }

    private List<AppointmentDetailDTO> buildAppointmentDetails(Set<AppointmentEntity> appointments) {
        return appointments.stream().map(a -> {
            AppointmentDetailDTO dto = new AppointmentDetailDTO();
            dto.setStartTime(a.getStartTime().toLocalDateTime());

            if (a.getDoctorEntity() != null)
                dto.setDoctorName(a.getDoctorEntity().getStaffEntity().getFullName());


            if (a.getServiceEntity() != null)
                dto.setServiceName(a.getServiceEntity().getProductEntity().getName());

            if (a.getServiceEntity() != null && a.getServiceEntity().getDepartmentEntity() != null)
                dto.setDepartmentName(a.getServiceEntity().getDepartmentEntity().getName());

            dto.setStatus(a.getAppointmentStatus() != null ? a.getAppointmentStatus().name() : "Chưa rõ");

            return dto;
        }).toList();
    }
}
