package org.project.service;

import java.util.List;

public interface TestRequestItemService {
    void createTestRequestItem(Long testRequestId, List<Long> testIds);
}
