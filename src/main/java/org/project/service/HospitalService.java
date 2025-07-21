package org.project.service;

import org.project.model.response.HospitalResponse;
import org.springframework.data.domain.Page;

public interface HospitalService {
    Page<HospitalResponse> getHospitals(int index, int size);

    Page<HospitalResponse> searchHospitalsByKeyword(int index, int size, String keyword);
}
