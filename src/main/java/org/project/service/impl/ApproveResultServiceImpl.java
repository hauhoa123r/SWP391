package org.project.service.impl;

import org.project.converter.ApproveResultFilterConverter;
import org.project.model.dto.ApproveResultDTO;
import org.project.model.response.ApproveResultFilterResponse;
import org.project.service.ApproveResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ApproveResultServiceImpl implements ApproveResultService {


    @Autowired
    private ApproveResultFilterConverter approveResultFilterConverter;

    @Override
    public Page<ApproveResultFilterResponse> getAllApproveResult(ApproveResultDTO approveResultDTO) throws IllegalAccessException {
        Page<ApproveResultFilterResponse> approveResultFilterResponses = approveResultFilterConverter.toConverterApproveResultFilterResponse(approveResultDTO);
        return approveResultFilterResponses;
    }
}
