package org.project.converter;

import org.project.entity.TestTypeEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.FilterSampleNameDTO;
import org.project.model.dto.SampleFilterDTO;
import org.project.model.response.SampleFilterResponse;
import org.project.repository.SampleScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SampleFilterConverter {

    @Autowired
    private SampleScheduleRepository sampleScheduleRepository;

    public Page<SampleFilterResponse> toSampleFilterDTOPage(FilterSampleNameDTO filterSampleNameDTO) throws IllegalAccessException {
        filterSampleNameDTO.setStatus("active");
        Page<TestTypeEntity> testTypeEntities = sampleScheduleRepository.filterNameSample(filterSampleNameDTO);
        if (testTypeEntities == null) {
            throw new ResourceNotFoundException("Not found");
        }

        return testTypeEntities.map(testTypeEntity -> {
               SampleFilterResponse sampleFilterResponse = new SampleFilterResponse();
               sampleFilterResponse.setId(testTypeEntity.getId());
               sampleFilterResponse.setName(testTypeEntity.getTestTypeName());
               return sampleFilterResponse;
        });
    }


    public Page<SampleFilterResponse> toSampleFilterDTOInactive(FilterSampleNameDTO filterSampleNameDTO) throws IllegalAccessException {
        filterSampleNameDTO.setStatus("inactive");
        Page<TestTypeEntity> testTypeEntities = sampleScheduleRepository.filterNameSample(filterSampleNameDTO);
        if (testTypeEntities == null) {
            throw new ResourceNotFoundException("Not found");
        }

        return testTypeEntities.map(testTypeEntity -> {
            SampleFilterResponse sampleFilterResponse = new SampleFilterResponse();
            sampleFilterResponse.setId(testTypeEntity.getId());
            sampleFilterResponse.setName(testTypeEntity.getTestTypeName());
            return sampleFilterResponse;
        });
    }
}
