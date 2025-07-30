package org.project.service;

import org.project.model.dto.ApproveResultDTO;
import org.project.model.response.ApproveResultFilterResponse;
import org.springframework.data.domain.Page;

public interface ApproveResultService
{
    Page<ApproveResultFilterResponse> getAllApproveResult(ApproveResultDTO approveResultDTO) throws IllegalAccessException;
    Boolean isApproveResultExist(Long id);
}
