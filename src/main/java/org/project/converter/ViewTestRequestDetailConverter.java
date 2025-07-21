package org.project.converter;

import org.project.entity.AppointmentEntity;
import org.project.entity.SampleEntity;
import org.project.entity.TestRequestEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.response.SampleResponse;
import org.project.model.response.ViewResultResponse;
import org.project.model.response.ViewTestRequestDetailResponse;
import org.project.repository.AppointmentRepository;
import org.project.repository.AssignmentRepository;
import org.project.service.TestTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ViewTestRequestDetailConverter {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TestTypeRepository testTypeRepository;


    @Autowired
    private AppointmentRepository appointmentRepositoryImpl;

    public ViewTestRequestDetailResponse viewTestRequestDetailConverter(Long id){
        Optional<AppointmentEntity> appointmentEntity = appointmentRepositoryImpl.findById(id);

        if(!appointmentEntity.isPresent()){
            throw new ResourceNotFoundException("Can not find appointment by id: " + id);
        }

        ViewResultResponse viewResultResponse = new ViewResultResponse();
        viewResultResponse.setPatientName(appointmentEntity.get().getPatientEntity().getFullName());
        viewResultResponse.setDepartment(appointmentEntity.get().getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
        viewResultResponse.setDoctorName(appointmentEntity.get().getDoctorEntity().getStaffEntity().getFullName());

        List<TestRequestEntity> testRequestEntities = (List<TestRequestEntity>) appointmentEntity.get().getTestRequestEntities();


        return null;
    }
}
