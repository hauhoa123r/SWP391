package org.project.api;

import jakarta.servlet.http.HttpSession;
import org.project.entity.UserEntity;
import org.project.model.dto.AppointmentDTO;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppointmentAPI {

    private AppointmentService appointmentService;

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/api/patient/appointment")
    public ResponseEntity<String> saveAppointment(HttpSession session, @RequestBody AppointmentDTO appointmentDTO) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        appointmentDTO.setPatientEntityUserEntityId(user.getId());
        appointmentService.saveAppointment(appointmentDTO);
        return ResponseEntity.ok("Appointment created successfully");
    }
}
