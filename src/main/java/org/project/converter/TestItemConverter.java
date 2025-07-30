package org.project.converter;

import org.project.entity.*;
import org.project.model.dto.SearchTestTypeDTO;
import org.project.model.response.SetResultResponse;
import org.project.model.response.TestRequestResponse;
import org.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TestItemConverter {
    @Autowired
    private AssignmentRepository assignmentRepositoryImpl;

    @Autowired
    private PatientRepository patientRepositoryImpl;

    @Autowired
    private TestItemRepository testItemRepositoryImpl;

    @Autowired
    private SampleScheduleRepository sampleScheduleRepositoryImpl;

    @Autowired
    private TestTypeRepository testTypeRepositoryImpl;

    public SetResultResponse toConverterSetResultResponse(Long id){
        Optional<SampleEntity> sampleEntity = sampleScheduleRepositoryImpl.findById(id);
        TestRequestEntity testRequestEntity = sampleEntity.get().getTestRequest();
        PatientEntity patientEntity = patientRepositoryImpl.findById(testRequestEntity.getAppointmentEntity().getPatientEntity().getId()).get();
        List<TestItemEntity> testItemEntityEntities = testItemRepositoryImpl.findByTestTypeEntity_Id(testRequestEntity.getTestTypeEntity().getId());
        SetResultResponse result = new SetResultResponse();

        result.setTestTypeName(testRequestEntity.getTestTypeEntity().getTestTypeName());
        result.setPatientName(patientEntity.getFullName());
        result.setManagerName(testRequestEntity.getAppointmentEntity().getDoctorEntity().getStaffEntity().getFullName());
        result.setDepartmentName(testRequestEntity.getAppointmentEntity().getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
        result.setRequestAt(testRequestEntity.getRequestTime().toString());
        result.setId(sampleEntity.get().getId());
        List<String> unitName = new ArrayList<>();
        List<String> testItemName = new ArrayList<>();

        for(TestItemEntity item: testItemEntityEntities){
            String unit = item.getUnit();
            unitName.add(unit);
            String itemName = item.getName();
            testItemName.add(itemName);
        }
        result.setTestItemName(testItemName);
        result.setUnitName(unitName);
        result.setTestTypeId(testRequestEntity.getTestTypeEntity().getId());
        result.setImagePatient(patientEntity.getAvatarUrl() != null ? patientEntity.getAvatarUrl() : "");
        return result;
    }


    public Page<TestRequestResponse> toFilterTestRequestResponse(SearchTestTypeDTO searchTestTypeDTO) {
        Page<TestTypeEntity> testTypeEntities = testTypeRepositoryImpl.toFilterTestRequestResponse(searchTestTypeDTO);

        Page<TestRequestResponse> testRequestResponses = testTypeEntities.map(entity -> {
            TestRequestResponse testRequestResponse = new TestRequestResponse();
            testRequestResponse.setTestType(entity.getTestTypeName());
            testRequestResponse.setTestTypeId(String.valueOf(entity.getId()));
            return testRequestResponse;
        });

        return testRequestResponses;
    }
}
