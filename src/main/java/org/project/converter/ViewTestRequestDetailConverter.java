package org.project.converter;

import org.project.entity.AppointmentEntity;
import org.project.entity.ResultDetailEntity;
import org.project.entity.ResultEntity;
import org.project.entity.TestRequestEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.response.ViewResultDetailResponse;
import org.project.model.response.ViewResultResponse;
import org.project.model.response.ViewTestRequestDetailResponse;
import org.project.repository.AppointmentRepository;
import org.project.repository.AssignmentRepository;
import org.project.repository.TestTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ViewTestRequestDetailConverter {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TestTypeRepository testTypeRepository;


    @Autowired
    private AppointmentRepository appointmentRepositoryImpl;

    public ViewResultResponse viewTestRequestDetailConverter(Long id){

        Optional<AppointmentEntity> appointmentEntity = appointmentRepositoryImpl.findById(id);

        if(!appointmentEntity.isPresent()){
            throw new ResourceNotFoundException("Can not find appointment by id: " + id);
        }

        ViewResultResponse viewResultResponse = new ViewResultResponse();
        viewResultResponse.setPatientName(appointmentEntity.get().getPatientEntity().getFullName());
        viewResultResponse.setDepartment(appointmentEntity.get().getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
        viewResultResponse.setDoctorName(appointmentEntity.get().getDoctorEntity().getStaffEntity().getFullName());
        viewResultResponse.setImagePatient(appointmentEntity.get().getPatientEntity().getAvatarUrl() != null ? appointmentEntity.get().getPatientEntity().getAvatarUrl() : "" );
        viewResultResponse.setId(appointmentEntity.get().getId());
        List<ViewResultDetailResponse> viewResultDetailResponses = new ArrayList<>();
        appointmentEntity.get().getTestRequestEntities().forEach(testRequestEntity -> {
            ViewResultDetailResponse viewResultDetailResponse = new ViewResultDetailResponse();
            viewResultDetailResponse.setSampleName(testRequestEntity.getTestTypeEntity().getTestTypeName());
            viewResultDetailResponse.setManagerName("Nguyen Van A");
            viewResultDetailResponse.setStatus(testRequestEntity.getRequestStatus().getValue());
            viewResultDetailResponses.add(viewResultDetailResponse);
        });
        StringBuilder noteResult = new StringBuilder();
        if(isCompleted(appointmentEntity.get().getTestRequestEntities())){
            noteResult.append(converterResult(appointmentEntity.get().getTestRequestEntities()) + "\n");
        }
        if(isReject(appointmentEntity.get().getTestRequestEntities())){
            noteResult.append(converterReasonReject(appointmentEntity.get().getTestRequestEntities()));
        }
        viewResultResponse.setDateRequest(String.valueOf(appointmentEntity.get().getStartTime()));
        viewResultResponse.setNote(noteResult.toString() != null ? noteResult.toString() : "");
        viewResultResponse.setViewResultDetailResponses(viewResultDetailResponses);
        return viewResultResponse;
    }

    private String converterReasonReject(Set<TestRequestEntity> testRequestEntities){
        StringBuilder reason = new StringBuilder("Lý do không lấy được mẫu ");
        for(TestRequestEntity testRequestEntity: testRequestEntities){
            if(testRequestEntity.getRequestStatus().getValue().equals("rejected")){
                reason.append(testRequestEntity.getTestTypeEntity().getTestTypeName() + ": ");
                reason.append(testRequestEntity.getReason() + "\n");
            }
        }
        return reason.toString();
    }
    private String converterDataUnit(ResultEntity resultEntity){
        StringBuilder dataUnit = new StringBuilder();
        List<ResultDetailEntity> resultDetailEntity = resultEntity.getResultDetailEntities();
        for(ResultDetailEntity resultDetail: resultDetailEntity){
            dataUnit.append("Chỉ số " + resultDetail.getTestItemEntity().getName() + ": " + resultDetail.getValue().longValue() + "\n" );

        }
        return dataUnit.toString();
    }

    private String converterResult(Set<TestRequestEntity> testRequestEntities){
        StringBuilder results = new StringBuilder("Kết quả:\n");
        for(TestRequestEntity testRequestEntity: testRequestEntities){
            if(testRequestEntity.getRequestStatus().getValue().equals("completed")){
                results.append(converterDataUnit(testRequestEntity.getSamples().getResults()));
                results.append(testRequestEntity.getSamples().getResults().getDataunit()+ "\n");
            }
        }
        return results.toString();
    }

    private Boolean isReject(Set<TestRequestEntity> testRequestEntities){
        for(TestRequestEntity testRequestEntity: testRequestEntities){
            if(testRequestEntity.getRequestStatus().getValue().equals("rejected")){
                return true;
            }
        }
        return false;
    }
    private Boolean isCompleted(Set<TestRequestEntity> testRequestEntities){
        for(TestRequestEntity testRequestEntity: testRequestEntities){
            if(testRequestEntity.getRequestStatus().getValue().equals("completed")){
                return true;
            }
        }
        return false;
    }
}
