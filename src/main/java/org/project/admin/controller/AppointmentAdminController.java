package org.project.admin.controller;

import org.project.admin.dto.request.AppointmentRequest;
import org.project.admin.dto.response.AppointmentResponse;
import org.project.admin.enums.appoinements.AppointmentStatus;
import org.project.admin.service.AppointmentService;
import org.project.admin.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentAdminController {

    private final AppointmentService appointmentService;

    @PostMapping
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request) {
        return appointmentService.create(request);
    }

    @GetMapping("/{id}")
    public AppointmentResponse getById(@PathVariable Long id) {
        return appointmentService.getById(id);
    }

    @GetMapping
    public PageResponse<AppointmentResponse> getPage(@PageableDefault(size = 20) Pageable pageable) {
        return appointmentService.getPage(pageable);
    }

    @GetMapping("/by-doctor/{doctorId}")
    public PageResponse<AppointmentResponse> getByDoctor(
            @PathVariable Long doctorId,
            @PageableDefault(size = 20) Pageable pageable) {
        return appointmentService.getByDoctorId(doctorId, pageable);
    }

    @GetMapping("/by-patient/{patientId}")
    public PageResponse<AppointmentResponse> getByPatient(
            @PathVariable Long patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        return appointmentService.getByPatientId(patientId, pageable);
    }

    @GetMapping("/by-status/{status}")
    public PageResponse<AppointmentResponse> getByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return appointmentService.getByStatus(status, pageable);
    }

    @GetMapping("/upcoming")
    public PageResponse<AppointmentResponse> getUpcoming(@PageableDefault(size = 20) Pageable pageable) {
        return appointmentService.getUpcoming(pageable);
    }

    @GetMapping("/upcoming-by-status")
    public PageResponse<AppointmentResponse> getUpcomingByStatus(
            @RequestParam List<AppointmentStatus> statuses,
            @PageableDefault(size = 20) Pageable pageable) {
        return appointmentService.getUpcomingByStatus(statuses, pageable);
    }

    @GetMapping("/pending")
    public PageResponse<AppointmentResponse> getPendingAppointments(@PageableDefault(size = 20) Pageable pageable) {
        return appointmentService.getPendingAppointments(pageable);
    }

    @PutMapping("/{id}")
    public AppointmentResponse update(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
        return appointmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        appointmentService.delete(id);
    }
}
