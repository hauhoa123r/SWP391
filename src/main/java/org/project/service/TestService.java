package org.project.service;

import org.project.model.response.TestListResponse;

import java.util.List;

public interface TestService {
    List<TestListResponse> getTestList();
    List<TestListResponse> getTestListLikeName(String name);
}
