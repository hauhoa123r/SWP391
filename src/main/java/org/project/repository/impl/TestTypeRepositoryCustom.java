package org.project.repository.impl;


import org.project.entity.TestTypeEntity;
import org.project.model.dto.SearchTestTypeDTO;
import org.project.model.response.TestRequestResponse;
import org.springframework.data.domain.Page;

public interface TestTypeRepositoryCustom {

    Page<TestTypeEntity> toFilterTestRequestResponse(SearchTestTypeDTO searchTestTypeDTO);
}
