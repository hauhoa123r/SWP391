package org.project.converter;

import org.project.entity.PatientEntity;
import org.project.entity.SampleEntity;
import org.project.entity.TestItemEntity;
import org.project.entity.TestRequestEntity;
import org.project.model.response.SetResultResponse;
import org.project.repository.AssignmentRepository;
import org.project.repository.PatientRepository;
import org.project.repository.SampleScheduleRepository;
import org.project.repository.TestItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return result;
    }
}
