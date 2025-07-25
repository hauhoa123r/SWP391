package org.project.api;

import org.project.model.response.AppointmentDetailsResponse;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/in-progress")
public class InProgressAPI {
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/{appoint_id}")
    public AppointmentDetailsResponse getAppointmentDetails(@PathVariable Long appoint_id) {
        return appointmentService.getAppointment(appoint_id);
    }
}
