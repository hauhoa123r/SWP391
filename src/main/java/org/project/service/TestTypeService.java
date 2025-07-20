package org.project.service;

import org.project.entity.TestTypeEntity;
import org.project.model.response.TestTypeListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TestTypeService {
    Page<TestTypeListResponse> searchTestTypes(String keyword, Pageable pageable);
    Page<TestTypeListResponse> getAllTestTypes(Pageable pageable);
}
