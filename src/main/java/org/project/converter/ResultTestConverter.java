package org.project.converter;

import org.project.entity.AppointmentEntity;
import org.project.entity.TestRequestEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.ApproveResultDTO;
import org.project.model.dto.ResultTestDTO;
import org.project.model.response.ResultAppointmentResponse;
import org.project.repository.ResultTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ResultTestConverter {

    @Autowired
    private ResultTestRepository resultTestRepository;

    public Page<ResultAppointmentResponse> filterResultSample(ResultTestDTO resultTestDTO) throws IllegalAccessException {
        Page<AppointmentEntity> appointmentEntities = resultTestRepository.filterAppointmentEntityCustom(resultTestDTO);
        return appointmentEntities.map(entity -> {
                ResultAppointmentResponse resultAppointmentResponse = new ResultAppointmentResponse();
                resultAppointmentResponse.setPatientName(entity.getPatientEntity().getFullName());
                resultAppointmentResponse.setId(entity.getId());
                resultAppointmentResponse.setDepartmentName(entity.getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
                resultAppointmentResponse.setDoctorName(entity.getDoctorEntity().getStaffEntity().getFullName());
                resultAppointmentResponse.setTestType(converterTestTypes(entity.getTestRequestEntities()).toString());
                return resultAppointmentResponse;
        });
    }

    private List<String> converterTestTypes(Set<TestRequestEntity> testRequestEntities){
        List<String> testTypes = new ArrayList<>();
        testRequestEntities.forEach(testRequestEntity -> {
            testTypes.add(testRequestEntity.getTestTypeEntity().getTestTypeName());
        });
        return testTypes;
    }
}
