package org.project.service;

import org.project.entity.VitalSignEntity;
import org.project.model.request.AddVitalSignRequest;
import org.project.model.response.VitalSignResponse;

import java.util.List;

public interface VitalSignService {
    boolean addVitalSign(Long medicalRecordId, AddVitalSignRequest addVitalSignRequest);
    boolean updateVitalSign(Long vitalSignId, AddVitalSignRequest addVitalSignRequest);
    boolean deleteVitalSign(Long vitalSignId);
    List<VitalSignResponse> getVitalSign(Long medicalRecordId);

}
