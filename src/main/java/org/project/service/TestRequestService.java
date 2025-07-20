package org.project.service;

import org.project.model.request.TestListRequest;
import org.project.model.response.TestRequestInAppointment;

import java.util.List;

public interface TestRequestService {
    Boolean addListTestRequestByAppointmentId(TestListRequest testListRequest);
    List<TestRequestInAppointment> getListTestRequest(Long appointmentId);
    Boolean deleteTestRequest(Long testRequestId);
}
