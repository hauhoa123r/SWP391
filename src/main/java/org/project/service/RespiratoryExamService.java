package org.project.service;

import org.project.model.request.AddRespiratoryRequest;
import org.project.model.request.AddVitalSignRequest;
import org.project.model.response.RespiratoryResponse;
import org.project.model.response.VitalSignResponse;

import java.util.List;

public interface RespiratoryExamService {
    boolean addRespiratory(Long medicalRecordId, AddRespiratoryRequest addRespiratoryRequest);
    boolean updateRespiratory(Long respiratoryId, AddRespiratoryRequest addRespiratoryRequest);
    boolean deleteRespiratory(Long respiratoryId);
    List<RespiratoryResponse> getRespiratory(Long medicalRecordId);
}
