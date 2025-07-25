package org.project.service;

import org.project.entity.ResultEntity;
import org.project.model.dto.ApproveResultDTO;
import org.project.model.dto.ResultTestDTO;
import org.project.model.response.ResultAppointmentResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ResultSampleService {
     ResultEntity isSaveResultSample(Map<String, String> dataUnit);
     Page<ResultAppointmentResponse> filterResultSample(ResultTestDTO resultTestDTO) throws IllegalAccessException;
     Boolean approveResultSample(Long id);
}
