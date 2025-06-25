package org.project.service;

import org.project.model.response.TestRequestResponse;

public interface TestRequestService {
    Long createTestRequest(Long appointmentId);
    TestRequestResponse getTestRequest(Long appointmentId);
}
