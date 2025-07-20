package org.project.service.impl;

import org.project.model.request.AddNeurologicRequest;
import org.project.model.response.NeurologicResponse;
import org.project.service.NeurologicExamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NeurologicExamServiceImpl implements NeurologicExamService {

    @Override
    public boolean addNeurologic(Long medicalRecordId, AddNeurologicRequest addNeurologicRequest) {
        return false;
    }

    @Override
    public boolean updateNeurologic(Long neurologicId, AddNeurologicRequest addNeurologicRequest) {
        return false;
    }

    @Override
    public boolean deleteNeurologic(Long neurologicId) {
        return false;
    }

    @Override
    public List<NeurologicResponse> getNeurologic(Long medicalRecordId) {
        return List.of();
    }
}
