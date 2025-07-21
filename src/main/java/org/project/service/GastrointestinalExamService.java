package org.project.service;

import org.project.model.request.AddGastrointestinalRequest;
import org.project.model.request.AddRespiratoryRequest;
import org.project.model.response.GastrointestinalResponse;

import java.util.List;

public interface GastrointestinalExamService {
    boolean addGastrointestinal(Long medicalRecordId, AddGastrointestinalRequest addGastrointestinalRequest);
    boolean updateGastrointestinal(Long gastrointestinalId, AddGastrointestinalRequest addGastrointestinalRequest);
    boolean deleteGastrointestinal(Long gastrointestinalId);
    List<GastrointestinalResponse> getGastrointestinal(Long medicalRecordId);

}
