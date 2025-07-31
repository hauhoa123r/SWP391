package org.project.converter;

import org.project.entity.SampleEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.ApproveResultDTO;
import org.project.model.response.ApproveResultFilterResponse;
import org.project.repository.ResultSampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ApproveResultFilterConverter
{
    @Autowired
    private ResultSampleRepository resultSampleRepository;

    public Page<ApproveResultFilterResponse> toConverterApproveResultFilterResponse(ApproveResultDTO approveResultDTO) throws IllegalAccessException {
        approveResultDTO.setStatus("entered");
        Page<SampleEntity> sampleEntities = resultSampleRepository.filterSampleEntityCustom(approveResultDTO);
        if (sampleEntities == null) {
            throw new ResourceNotFoundException("Cam not get Sample Entity by search");
        }

        return sampleEntities.map(entity -> {
            ApproveResultFilterResponse approveResultFilterResponse = new ApproveResultFilterResponse();
            approveResultFilterResponse.setPatientName(entity.getTestRequest().getAppointmentEntity().getPatientEntity().getFullName());
            approveResultFilterResponse.setResultId(entity.getResults().getId());
            approveResultFilterResponse.setTestType(entity.getTestRequest().getTestTypeEntity().getTestTypeName());
            approveResultFilterResponse.setTester("Nguyen Van A");
            approveResultFilterResponse.setRequestAt(String.valueOf(entity.getCollectionTime()));
            approveResultFilterResponse.setStatus(entity.getResults().getStatus());
            return approveResultFilterResponse;
        });
    }

}
