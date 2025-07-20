package org.project.service.impl;

import org.project.model.request.AddGastrointestinalRequest;
import org.project.model.response.GastrointestinalResponse;
import org.project.service.GastrointestinalExamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GastrointestinalExamServiceImpl implements GastrointestinalExamService {
    @Override
    public boolean addGastrointestinal(Long medicalRecordId, AddGastrointestinalRequest addGastrointestinalRequest) {
        return false;
    }

    @Override
    public boolean updateGastrointestinal(Long gastrointestinalId, AddGastrointestinalRequest addGastrointestinalRequest) {
        return false;
    }

    @Override
    public boolean deleteGastrointestinal(Long gastrointestinalId) {
        return false;
    }

    @Override
    public List<GastrointestinalResponse> getGastrointestinal(Long medicalRecordId) {
        return List.of();
    }
}
