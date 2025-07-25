package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.ApproveResultFilterConverter;
import org.project.entity.ResultDetailEntity;
import org.project.entity.ResultEntity;
import org.project.entity.UserEntity;
import org.project.exception.ErrorResponse;
import org.project.model.dto.ApproveResultDTO;
import org.project.model.response.ApproveResultFilterResponse;
import org.project.repository.ResultDetailRepository;
import org.project.repository.ResultSampleRepository;
import org.project.repository.UserRepository;
import org.project.service.ApproveResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ApproveResultServiceImpl implements ApproveResultService {


    @Autowired
    private ApproveResultFilterConverter approveResultFilterConverter;

    @Autowired
    private ResultSampleRepository resultSampleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<ApproveResultFilterResponse> getAllApproveResult(ApproveResultDTO approveResultDTO) throws IllegalAccessException {
        Page<ApproveResultFilterResponse> approveResultFilterResponses = approveResultFilterConverter.toConverterApproveResultFilterResponse(approveResultDTO);
        return approveResultFilterResponses;
    }

    @Override
    public Boolean isApproveResultExist(Long id) {
        UserEntity userEntity = userRepository.findById(1L).orElseThrow(() -> new ErrorResponse("Không tìm thấy id"));
        ResultEntity resultEntity = resultSampleRepository.findById(id).orElseThrow(() -> new ErrorResponse("Không tìm thấy id"));
        resultEntity.setResultEntryStatus("verified");
        resultEntity.setApprovedBy(userEntity);
        resultSampleRepository.save(resultEntity);
        return true;
    }
}
