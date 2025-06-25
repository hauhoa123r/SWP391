package org.project.service.impl;

import org.project.entity.TestEntity;
import org.project.entity.TestRequestEntity;
import org.project.entity.TestRequestItemEntity;
import org.project.entity.TestRequestItemEntityId;
import org.project.repository.TestRepository;
import org.project.repository.TestRequestItemRepository;
import org.project.repository.TestRequestRepository;
import org.project.service.TestRequestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        TestRequestItemEntityId testRequestItemEntityId = new TestRequestItemEntityId();
        TestRequestEntity testRequestEntity = testRequestRepository.findById(testRequestId).get();
        testRequestItemEntityId.setTestRequestId(testRequestId);
        for (Long testId : testIds) {
            TestEntity testEntity = testRepository.findById(testId).get();
            testRequestItemEntityId.setTestId(testId);
            TestRequestItemEntity testRequestItemEntity = new TestRequestItemEntity();
            testRequestItemEntity.setId(testRequestItemEntityId);
            testRequestItemEntity.setTestRequestEntity(testRequestEntity);
            testRequestItemEntity.setTestEntity(testEntity);
            testRequestItemRepository.save(testRequestItemEntity);
            System.out.println(testRequestItemEntity.toString());
        }
    }

    public static void main(String[] args) {
        TestRequestItemServiceImpl testRequestItemServiceImpl = new TestRequestItemServiceImpl();
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(8L);
        list.add(20L);
        testRequestItemServiceImpl.createTestRequestItem(13L,list);
    }
}
