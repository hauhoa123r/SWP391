package org.project.repository.impl;

import org.project.entity.SampleEntity;
import org.project.model.dto.ApproveResultDTO;
import org.springframework.data.domain.Page;

public interface ResultSampleRepositoryCustom {
    Page<SampleEntity> filterSampleEntityCustom(ApproveResultDTO approveResultDTO) throws IllegalAccessException;
}
