package org.project.converter;

import org.project.entity.HospitalEntity;
import org.project.model.dai.AppointmentDAI;
import org.project.model.dto.MakeAppointmentDTO;
import org.project.repository.HospitalRepository;
import org.springframework.stereotype.Component;

@Component
public class AppointmentConverter {

    private final HospitalRepository hospitalRepository;

    public AppointmentConverter(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public MakeAppointmentDTO toConverterMakeAppointmentDTO(AppointmentDAI appointmentDAI) {
        MakeAppointmentDTO makeAppointmentDTO = new MakeAppointmentDTO();
        makeAppointmentDTO.setDoctorName(appointmentDAI.getDoctorName());
        HospitalEntity hospitalEntity = hospitalRepository.findByNameContaining(appointmentDAI.getHospitalName());
        if(hospitalEntity == null) {
            makeAppointmentDTO.setHospitalName("Hospital does not exist in system!");
            makeAppointmentDTO.setHospitalAddress("?");
            makeAppointmentDTO.setDepartmentName("?");
        }else{
            makeAppointmentDTO.setHospitalName(hospitalEntity.getName());
            makeAppointmentDTO.setHospitalAddress(hospitalEntity.getAddress());
            makeAppointmentDTO.setDepartmentName(appointmentDAI.getDepartmentName());
        }
        makeAppointmentDTO.setStartDate(appointmentDAI.getDate() + " " + appointmentDAI.getTime());
        return makeAppointmentDTO;
    }
}
