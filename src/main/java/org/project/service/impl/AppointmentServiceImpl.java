package org.project.service.impl;

import org.project.converter.toAppointmentDetailsResponse;
import org.project.converter.toAppointmentsResponse;
import org.project.entity.AppointmentsEntity;
import org.project.model.response.AppointmentDetailsResponse;
import org.project.model.response.AppointmentsResponse;
import org.project.repository.AppointmentRepository;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private toAppointmentsResponse toAppoint;
    @Autowired
    private toAppointmentDetailsResponse toAppointDetails;
    @Override
    public List<AppointmentsResponse> getAllAppointments(Long doctorId) {
        List<AppointmentsEntity> appointments = appointmentRepository.findByDoctorId(doctorId);
        return appointments.stream().map(appointment ->
            toAppoint.toAppointmentResponse(appointment)
        ).collect(Collectors.toList());
    }

    @Override
    public AppointmentDetailsResponse getAppointment(Long id) {
        Optional<AppointmentsEntity> appointment = appointmentRepository.findById(id);
        return toAppointDetails.toAppointmentDetailsResponse(appointment.get());
    }
}
