package org.project.service.impl;

import org.project.entity.TestEntity;
import org.project.entity.TestRequestEntity;
import org.project.entity.TestRequestItemEntity;
import org.project.repository.TestRepository;
import org.project.repository.TestRequestItemRepository;
import org.project.repository.TestRequestRepository;
import org.project.service.TestRequestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestRequestItemServiceImpl implements TestRequestItemService {

    @Autowired
    private TestRequestItemRepository testRequestItemRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestRequestRepository testRequestRepository;
    @Override
    public void createTestRequestItem(Long testRequestId, List<Long> testIds) {
        TestRequestEntity testRequestEntity = testRequestRepository.findById(testRequestId).get();
        for (Long testId : testIds) {
            TestEntity testEntity = testRepository.findById(testId).get();
            TestRequestItemEntity testRequestItemEntity = new TestRequestItemEntity();
            testRequestItemEntity.setTestRequestEntity(testRequestEntity);
            testRequestItemEntity.setTestEntity(testEntity);
            testRequestItemRepository.save(testRequestItemEntity);
        }
    }
}
