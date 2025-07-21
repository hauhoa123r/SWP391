package org.project.service;

import org.project.model.request.AddMusculoskeletalRequest;
import org.project.model.request.AddRespiratoryRequest;
import org.project.model.response.MusculoskeletalResponse;
import org.project.model.response.RespiratoryResponse;

import java.util.List;

public interface MusculoskeletalExamService {
    boolean addMusculoskeletal(Long medicalRecordId, AddMusculoskeletalRequest addMusculoskeletalRequest);
    boolean updateMusculoskeletal(Long musculoskeletalId, AddMusculoskeletalRequest addMusculoskeletalRequest);
    boolean deleteMusculoskeletal(Long musculoskeletalId);
    List<MusculoskeletalResponse> getMusculoskeletal(Long medicalRecordId);

}
