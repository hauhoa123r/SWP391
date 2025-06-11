package org.project.service.impl;

import org.project.converter.TestConverter;
import org.project.entity.TestEntity;
import org.project.model.response.TestListResponse;
import org.project.repository.TestRepository;
import org.project.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestConverter testConverter;
    @Override
    public List<TestListResponse> getTestList() {
        List<TestEntity> testEntities = testRepository.findAll();
        return testEntities.stream().map(testConverter::toTestListResponse).toList();
    }

    @Override
    public List<TestListResponse> getTestListLikeName(String name) {
        List<TestEntity>  testEntities = testRepository.findTop10ByProductEntityNameContainingIgnoreCase(name);
        return testEntities.stream().map(testConverter::toTestListResponse).toList();
    }
}
