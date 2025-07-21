package org.project.service;

import org.project.model.request.AddDermatologicRequest;
import org.project.model.request.AddRespiratoryRequest;
import org.project.model.response.DermatologicResponse;
import org.project.model.response.RespiratoryResponse;

import java.util.List;

public interface DermatologicExamService {
    boolean addDermatologic(Long medicalRecordId, AddDermatologicRequest addDermatologicRequest);
    boolean updateDermatologic(Long dermatologicId, AddDermatologicRequest addDermatologicRequest);
    boolean deleteDermatologic(Long dermatologicId);
    List<DermatologicResponse> getDermatologic(Long medicalRecordId);

}
