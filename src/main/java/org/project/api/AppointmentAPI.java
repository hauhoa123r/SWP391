package org.project.api;

import jakarta.servlet.http.HttpSession;
import org.project.model.dto.AppointmentDTO;
import org.project.model.response.UserResponse;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentAPI {

    private AppointmentService appointmentService;

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<String> saveAppointment(HttpSession session, @RequestBody AppointmentDTO appointmentDTO) {
        UserResponse user = (UserResponse) session.getAttribute("user");
        appointmentDTO.setPatientEntityUserEntityId(user.getId());
        Map<String, Object> response = appointmentService.saveAppointment(appointmentDTO);
        if (response.containsKey("success") && (boolean) response.get("success")) {
            return ResponseEntity.ok("Appointment saved successfully.");
        }
        return ResponseEntity.badRequest().body(
                response.containsKey("message") ? (String) response.get("message") : "Failed to save appointment."
        );
    }
}
