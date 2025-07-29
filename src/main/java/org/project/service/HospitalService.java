package org.project.service;

import org.project.model.dto.HospitalDTO;
import org.project.model.response.HospitalResponse;
import org.springframework.data.domain.Page;

public interface HospitalService {
    HospitalResponse getHospital(Long id);

    Page<HospitalResponse> getHospitals(int index, int size, HospitalDTO hospitalDTO);

    Page<HospitalResponse> getHospitals(int index, int size);

    Page<HospitalResponse> searchHospitalsByKeyword(int index, int size, String keyword);

    void createHospital(HospitalDTO hospitalDTO);

    void updateHospital(Long hospitalId, HospitalDTO hospitalDTO);

    void deleteHospital(Long hospitalId);
}
