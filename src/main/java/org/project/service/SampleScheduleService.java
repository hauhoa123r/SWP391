package org.project.service;

import org.project.model.dto.RejectSampleScheduleDTO;
import org.project.model.dto.SampleFilterDTO;
import org.project.model.request.CreateSamplePatientRequest;
import org.project.model.response.SampleScheduleResponse;
import org.springframework.data.domain.Page;

public interface SampleScheduleService {
    Page<SampleScheduleResponse> getAllSampleSchedule(SampleFilterDTO sampleFilterDTO) throws IllegalAccessException;
    Boolean createSampleSchedule(CreateSamplePatientRequest createSamplePatientRequest);
    Boolean rejectSampleSchedule(RejectSampleScheduleDTO rejectSampleScheduleDTO);
}
