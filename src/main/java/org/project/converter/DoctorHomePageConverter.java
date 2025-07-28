package org.project.converter;

import org.project.entity.AppointmentEntity;
import org.project.entity.UserEntity;
import org.project.model.response.DoctorHomepageResponse;
import org.project.model.response.TopThreeAppointmentNearest;
import org.project.repository.AppointmentRepository;
import org.project.repository.PatientRepository;
import org.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DoctorHomePageConverter {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    public DoctorHomepageResponse doctorHomepageResponse(Long id){
        DoctorHomepageResponse response = new DoctorHomepageResponse();
        UserEntity userEntity = userRepository.findById(id).get();
        response.setDoctorName(userEntity.getStaffEntity().getFullName());
        response.setTotalPatients(patientRepository.countPatientByHospital(
                userEntity.getStaffEntity().getHospitalEntity().getName()));
        response.setTotalAppointmentsToday((long) appointmentRepository.countTotalAppointmentsToday(userEntity.getStaffEntity().getDoctorEntity().getId()));
        List<TopThreeAppointmentNearest> topThreeAppointmentNearests = new ArrayList<>();
        List<AppointmentEntity> appointmentEntities = appointmentRepository
                .findThreeTopAppointmentNearest(userEntity.getStaffEntity()
                .getDoctorEntity().getId());
        for(AppointmentEntity appointmentEntity : appointmentEntities){
            TopThreeAppointmentNearest topThreeAppointment = new TopThreeAppointmentNearest();
            topThreeAppointment.setAppointmentId(appointmentEntity.getId());
            topThreeAppointment.setName(appointmentEntity.getPatientEntity().getFullName());
            topThreeAppointment.setTimeAppointment(String.valueOf(appointmentEntity.getStartTime()));
            topThreeAppointment.setDepartmentName(appointmentEntity
                    .getServiceEntity()
                    .getDepartmentEntity()
                    .getName());
            topThreeAppointmentNearests.add(topThreeAppointment);
        }
        response.setTopThreeAppointmentNearest(topThreeAppointmentNearests);
        return response;
    }
}
