package org.project.repository.impl;

import org.project.entity.SampleEntity;
import org.project.model.dto.SampleFilterDTO;
import org.springframework.data.domain.Page;

public interface SampleScheduleCustom {
    Page<SampleEntity> filterSampleEntityCustom(SampleFilterDTO sampleFilterDTO) throws IllegalAccessException;
}
