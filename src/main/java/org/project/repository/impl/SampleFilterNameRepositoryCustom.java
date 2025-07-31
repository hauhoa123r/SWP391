package org.project.repository.impl;

import org.project.entity.TestTypeEntity;
import org.project.model.dto.FilterSampleNameDTO;
import org.springframework.data.domain.Page;

public interface SampleFilterNameRepositoryCustom {
    Page<TestTypeEntity> filterNameSample(FilterSampleNameDTO filterSampleNameDTO) throws IllegalAccessException;
}
