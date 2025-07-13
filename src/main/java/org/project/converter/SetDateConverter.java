package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.SampleEntity;
import org.project.entity.StaffEntity;
import org.project.entity.TechnicianEntity;
import org.project.entity.TestRequestEntity;
import org.project.enums.StaffRole;
import org.project.enums.TechnicianRank;
import org.project.exception.ResourceNotFoundException;
import org.project.model.response.ManagerNameResponse;
import org.project.model.response.SetDateGetSampleResponse;
import org.project.repository.AssignmentRepository;
import org.project.repository.SampleScheduleRepository;
import org.project.repository.StaffRepository;
import org.project.repository.TechnicianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SetDateConverter {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private SampleScheduleRepository sampleScheduleRepository;

    public SetDateGetSampleResponse toConverterSetDateGetSampleResponse(Long id){
        Optional<TestRequestEntity> testRequestEntity = assignmentRepository.findById(id);
        List<StaffEntity> staffEntitiesRoleTechnician = staffRepository.findByStaffRoleAndHospitalEntity_IdAndTechnicianEntity_TechnicianRank(StaffRole.TECHNICIAN, 1L, TechnicianRank.TECHNICIAN);
        List<ManagerNameResponse> managerNameResponses = new ArrayList<>();
        staffEntitiesRoleTechnician.forEach(staffEntity -> {
            ManagerNameResponse managerNameResponse = new ManagerNameResponse();
            managerNameResponse.setId(staffEntity.getId());
            managerNameResponse.setName(staffEntity.getFullName());
            managerNameResponses.add(managerNameResponse);
        });

        if(!testRequestEntity.isPresent()){
            throw new ResourceNotFoundException("Can not find test request by id: " + id);
        }
        SampleEntity sampleEntity = sampleScheduleRepository.findByTestRequest_Id(testRequestEntity.get().getId());
        SetDateGetSampleResponse setDateGetSampleResponse = new SetDateGetSampleResponse();
        setDateGetSampleResponse.setSampleId(sampleEntity.getId());
        setDateGetSampleResponse.setTestType(testRequestEntity.get().getTestType().getTestTypeName());
        setDateGetSampleResponse.setPatientName(testRequestEntity.get().getAppointmentEntity().getPatientEntity().getFullName());
        setDateGetSampleResponse.setDepartmentName(testRequestEntity.get().getAppointmentEntity().getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
        setDateGetSampleResponse.setManagerName(managerNameResponses);
        return setDateGetSampleResponse;
    }
}
