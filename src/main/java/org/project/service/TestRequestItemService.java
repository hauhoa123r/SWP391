package org.project.service;

import java.util.List;

public interface TestRequestItemService {
    void createTestRequestItem(Long appoint_id, List<Long> testIds);
}
