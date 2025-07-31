package org.project.service;

import org.project.model.request.SampleRequestDTO;

public interface TestTypeService {
    Boolean isCreateTestType(SampleRequestDTO sampleRequestDTO);
    Boolean isDeleteTestType(Long id);
    Boolean isRestoreTestType(Long id);
}
