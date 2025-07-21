package org.project.service;

import org.project.model.request.AddGenitourinaryRequest;
import org.project.model.request.AddRespiratoryRequest;
import org.project.model.response.GenitourinaryResponse;

import java.util.List;

public interface GenitourinaryExamService {
    boolean addGenitourinary(Long medicalRecordId, AddGenitourinaryRequest addGenitourinaryRequest);
    boolean updateGenitourinary(Long respiratoryId, AddGenitourinaryRequest addGenitourinaryRequest);
    boolean deleteGenitourinary(Long respiratoryId);
    List<GenitourinaryResponse> getGenitourinary(Long medicalRecordId);

}
