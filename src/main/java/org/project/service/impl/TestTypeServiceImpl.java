package org.project.service.impl;

import org.project.converter.TestTypeConverter;
import org.project.entity.TestTypeEntity;
import org.project.model.response.TestTypeListResponse;
import org.project.repository.TestTypeRepository;
import org.project.service.TestTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestTypeServiceImpl implements TestTypeService {

    @Autowired
    private TestTypeRepository testTypeRepository;
    @Autowired
    private TestTypeConverter testTypeConverter;

    @Override
    public Page<TestTypeListResponse> searchTestTypes(String keyword, Pageable pageable) {
        Page<TestTypeEntity> typeEntities = testTypeRepository.findByTestTypeNameContainingIgnoreCase(keyword, pageable);

        return typeEntities.map(testTypeEntity -> testTypeConverter.toTestTypeListResponse(testTypeEntity));
    }
    @Override
    public Page<TestTypeListResponse> getAllTestTypes(Pageable pageable) {
        Page<TestTypeEntity> typeEntities = testTypeRepository.findAll(pageable);
        return typeEntities.map(testTypeEntity -> testTypeConverter.toTestTypeListResponse(testTypeEntity));
    }
}
