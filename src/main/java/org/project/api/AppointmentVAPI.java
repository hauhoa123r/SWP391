package org.project.api;

import org.project.model.dto.AppointmentChangeStatusDTO;
import org.project.model.response.AppointmentDetailResponse;
import org.project.model.response.AppointmentListResponse;
import org.project.service.AppointmentVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentVAPI {
    @Autowired
    private AppointmentVService appointmentService;

    @GetMapping("/{doctorId}")
    public Page<AppointmentListResponse> getAppointments(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(required = false) String specificDate
    ) {
        return appointmentService.searchAppointments(doctorId, page, size, search, status, dateFilter, specificDate);
    }

    @GetMapping("/in-progress/{id}")
    public AppointmentDetailResponse getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentDetail(id);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentChangeStatusDTO> updateAppointmentStatus(@PathVariable Long id, @RequestBody AppointmentChangeStatusDTO appointmentDTO) {
        appointmentDTO.setId(id);
        AppointmentChangeStatusDTO updatedAppointmentDTO = appointmentService.updateAppointmentStatus(appointmentDTO);
        return new ResponseEntity<>(updatedAppointmentDTO, HttpStatus.OK);
    }

}
