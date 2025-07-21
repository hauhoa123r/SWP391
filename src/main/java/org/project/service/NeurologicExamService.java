package org.project.service;

import org.project.model.request.AddNeurologicRequest;
import org.project.model.request.AddRespiratoryRequest;
import org.project.model.response.NeurologicResponse;

import java.util.List;

public interface NeurologicExamService {
    boolean addNeurologic(Long medicalRecordId, AddNeurologicRequest addNeurologicRequest);
    boolean updateNeurologic(Long neurologicId, AddNeurologicRequest addNeurologicRequest);
    boolean deleteNeurologic(Long neurologicId);
    List<NeurologicResponse> getNeurologic(Long medicalRecordId);
}
