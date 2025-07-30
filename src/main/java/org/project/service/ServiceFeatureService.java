package org.project.service;

import org.project.entity.ServiceFeatureEntity;
import org.project.model.dto.ServiceFeatureDTO;

public interface ServiceFeatureService {
    ServiceFeatureEntity save(ServiceFeatureDTO serviceFeatureDTO);
}
