package org.project.service.impl;

import org.project.model.request.AddGenitourinaryRequest;
import org.project.model.response.GenitourinaryResponse;
import org.project.service.GenitourinaryExamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenitourinaryExamServiceImpl implements GenitourinaryExamService {
    @Override
    public boolean addGenitourinary(Long medicalRecordId, AddGenitourinaryRequest addGenitourinaryRequest) {
        return false;
    }

    @Override
    public boolean updateGenitourinary(Long respiratoryId, AddGenitourinaryRequest addGenitourinaryRequest) {
        return false;
    }

    @Override
    public boolean deleteGenitourinary(Long respiratoryId) {
        return false;
    }

    @Override
    public List<GenitourinaryResponse> getGenitourinary(Long medicalRecordId) {
        return List.of();
    }
}
