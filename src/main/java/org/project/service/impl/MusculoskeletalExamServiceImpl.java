package org.project.service.impl;

import org.project.model.request.AddMusculoskeletalRequest;
import org.project.model.response.MusculoskeletalResponse;
import org.project.service.MusculoskeletalExamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusculoskeletalExamServiceImpl implements MusculoskeletalExamService {
    @Override
    public boolean addMusculoskeletal(Long medicalRecordId, AddMusculoskeletalRequest addMusculoskeletalRequest) {
        return false;
    }

    @Override
    public boolean updateMusculoskeletal(Long musculoskeletalId, AddMusculoskeletalRequest addMusculoskeletalRequest) {
        return false;
    }

    @Override
    public boolean deleteMusculoskeletal(Long musculoskeletalId) {
        return false;
    }

    @Override
    public List<MusculoskeletalResponse> getMusculoskeletal(Long medicalRecordId) {
        return List.of();
    }
}
