package org.project.service;

import org.project.model.response.DoctorHeaderResponse;

public interface DoctorService {
    DoctorHeaderResponse getDoctorByUserId(Long userId);
}
