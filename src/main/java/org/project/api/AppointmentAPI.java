package org.project.api;

import org.project.model.dto.AppointmentDTO;
import org.project.model.response.AppointmentDetailResponse;
import org.project.model.response.AppointmentListResponse;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentAPI {
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/{doctor_id}")
    public List<AppointmentListResponse> getAllAppointments(@PathVariable Long doctor_id) {
        return appointmentService.getAllAppointmentIsPendingOrConfirmed(doctor_id);
    }

    @GetMapping("/in-progress/{id}")
    public AppointmentDetailResponse getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentDetail(id);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(@PathVariable Long id, @RequestBody AppointmentDTO appointmentDTO) {
        appointmentDTO.setId(id);
        AppointmentDTO updatedAppointmentDTO = appointmentService.updateAppointmentStatus(appointmentDTO);
        return new ResponseEntity<>(updatedAppointmentDTO, HttpStatus.OK);
    }

}
