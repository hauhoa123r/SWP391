package org.project.service;

import org.project.model.response.DoctorHeaderResponse;

public interface DoctorVService {
    DoctorHeaderResponse getDoctorByUserId(Long userId);
}