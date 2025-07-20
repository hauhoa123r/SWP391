package org.project.service.impl;

import org.project.model.request.AddDermatologicRequest;
import org.project.model.response.DermatologicResponse;
import org.project.service.DermatologicExamService;

import java.util.List;

public class DermatologicExamServiceImpl implements DermatologicExamService {
    @Override
    public boolean addDermatologic(Long medicalRecordId, AddDermatologicRequest addDermatologicRequest) {
        return false;
    }

    @Override
    public boolean updateDermatologic(Long dermatologicId, AddDermatologicRequest addDermatologicRequest) {
        return false;
    }

    @Override
    public boolean deleteDermatologic(Long dermatologicId) {
        return false;
    }

    @Override
    public List<DermatologicResponse> getDermatologic(Long medicalRecordId) {
        return List.of();
    }
}
