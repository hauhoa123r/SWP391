package org.project.api;

import org.project.model.response.AppointmentDetailsResponse;
import org.project.model.response.AppointmentsResponse;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentAPI {
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/{doctor_id}")
    public List<AppointmentsResponse> getAllAppointments(@PathVariable Long doctor_id) {
        return appointmentService.getAllAppointments(doctor_id);
    }
}
