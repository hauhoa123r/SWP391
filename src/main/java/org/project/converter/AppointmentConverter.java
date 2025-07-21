package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.AppointmentEntity;
import org.project.entity.HospitalEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dai.AppointmentDAI;
import org.project.model.dto.AppointmentDTO;
import org.project.model.dto.MakeAppointmentDTO;
import org.project.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppointmentConverter {

    private ModelMapperConfig modelMapperConfig;
    private HospitalRepository hospitalRepository;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    @Autowired
    public void setHospitalRepository(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public AppointmentEntity toEntity(AppointmentDTO appointmentDTO) {
        Optional<AppointmentEntity> appointmentEntity = Optional.ofNullable(modelMapperConfig.mapper().map(appointmentDTO, AppointmentEntity.class));
        return appointmentEntity.orElseThrow(() -> new ErrorMappingException(AppointmentDTO.class, AppointmentEntity.class));
    }

    public MakeAppointmentDTO toConverterMakeAppointmentDTO(AppointmentDAI appointmentDAI) {
        MakeAppointmentDTO makeAppointmentDTO = new MakeAppointmentDTO();
        makeAppointmentDTO.setDoctorName(appointmentDAI.getDoctorName());
        HospitalEntity hospitalEntity = hospitalRepository.findByNameContaining(appointmentDAI.getHospitalName());
        if (hospitalEntity == null) {
            makeAppointmentDTO.setHospitalName("Hospital does not exist in system!");
            makeAppointmentDTO.setHospitalAddress("?");
            makeAppointmentDTO.setDepartmentName("?");
        } else {
            makeAppointmentDTO.setHospitalName(hospitalEntity.getName());
            makeAppointmentDTO.setHospitalAddress(hospitalEntity.getAddress());
            makeAppointmentDTO.setDepartmentName(appointmentDAI.getDepartmentName());
        }
        makeAppointmentDTO.setStartDate(appointmentDAI.getDate() + " " + appointmentDAI.getTime());
        return makeAppointmentDTO;
    }
}
