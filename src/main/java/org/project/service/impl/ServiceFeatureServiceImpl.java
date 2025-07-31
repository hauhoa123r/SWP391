package org.project.service.impl;

import org.project.converter.ServiceFeatureConverter;
import org.project.entity.ServiceFeatureEntity;
import org.project.model.dto.ServiceFeatureDTO;
import org.project.repository.ServiceFeatureRepository;
import org.project.service.ServiceFeatureService;
import org.project.utils.MergeObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ServiceFeatureServiceImpl implements ServiceFeatureService {
    private ServiceFeatureRepository serviceFeatureRepository;
    private ServiceFeatureConverter serviceFeatureConverter;

    @Autowired
    public void setServiceFeatureRepository(ServiceFeatureRepository serviceFeatureRepository) {
        this.serviceFeatureRepository = serviceFeatureRepository;
    }

    @Autowired
    public void setServiceFeatureConverter(ServiceFeatureConverter serviceFeatureConverter) {
        this.serviceFeatureConverter = serviceFeatureConverter;
    }

    @Override
    public ServiceFeatureEntity save(ServiceFeatureDTO serviceFeatureDTO) {
        if (serviceFeatureDTO == null) {
            return null;
        }

        if (serviceFeatureDTO.getId() != null && serviceFeatureRepository.existsById(serviceFeatureDTO.getId())) {
            Optional<ServiceFeatureEntity> serviceFeatureEntityOptional = serviceFeatureRepository.findById(serviceFeatureDTO.getId());
            if (serviceFeatureEntityOptional.isEmpty()) {
                return null; // or throw an exception if preferred
            }
            ServiceFeatureEntity target = serviceFeatureEntityOptional.get();
            ServiceFeatureEntity source = serviceFeatureConverter.toEntity(serviceFeatureDTO);
            MergeObjectUtils.mergeNonNullFields(source, target);
            return serviceFeatureRepository.save(target);
        }
        ServiceFeatureEntity serviceFeatureEntity = serviceFeatureConverter.toEntity(serviceFeatureDTO);
        return serviceFeatureRepository.save(serviceFeatureEntity);
    }
}
