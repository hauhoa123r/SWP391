package org.project.api;

import jakarta.servlet.http.HttpSession;
import org.project.entity.UserEntity;
import org.project.enums.AppointmentStatus;
import org.project.model.dto.AppointmentDTO;
import org.project.model.dto.ChangeAppointmentDTO;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/api/staff/confirm/{appointmentId}")
    public ResponseEntity<String> confirmAppointment(@PathVariable Long appointmentId
            , @RequestParam Long scheduleCoordinatorId) {
        boolean isUpdated = appointmentService.changeStatus(appointmentId, AppointmentStatus.CONFIRMED, scheduleCoordinatorId);
        if (isUpdated) {
            return ResponseEntity.ok("Appointment confirmed successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to confirm appointment.");
    }

    @PatchMapping("/api/staff/cancel/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId
            , @RequestParam Long scheduleCoordinatorId) {
        boolean isUpdated = appointmentService.changeStatus(appointmentId, AppointmentStatus.CANCELLED, scheduleCoordinatorId);
        if (isUpdated) {
            return ResponseEntity.ok("Appointment canceled successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to cancel appointment.");
    }

    @PatchMapping("/api/staff/change")
    public ResponseEntity<String> changeAppointmentAndApproval(@RequestBody ChangeAppointmentDTO changeAppointmentDTO) {
        boolean isChanged = appointmentService.changeAppointment(changeAppointmentDTO);
        if (isChanged) {
            return ResponseEntity.ok("Appointment changed successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to change appointment.");
    }
}
