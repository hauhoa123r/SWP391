package org.project.service;

import org.project.model.response.DoctorResponse;

public interface DoctorService {
    DoctorResponse getDoctorByUserId(Long userId);
}
