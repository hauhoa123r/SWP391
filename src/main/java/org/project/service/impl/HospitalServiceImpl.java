package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.HospitalConverter;
import org.project.entity.HospitalEntity;
import org.project.exception.page.InvalidPageException;
import org.project.exception.page.PageNotFoundException;
import org.project.model.response.HospitalResponse;
import org.project.repository.HospitalRepository;
import org.project.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class HospitalServiceImpl implements HospitalService {

    private HospitalRepository hospitalRepository;
    private HospitalConverter hospitalConverter;

    @Autowired
    public void setHospitalRepository(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Autowired
    public void setHospitalConverter(HospitalConverter hospitalConverter) {
        this.hospitalConverter = hospitalConverter;
    }

    @Override
    public Page<HospitalResponse> getHospitals(int index, int size) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }
        Pageable pageable = PageRequest.of(index, size);
        Page<HospitalEntity> hospitalEntityPage = hospitalRepository.findAll(pageable);
        if (hospitalEntityPage.isEmpty()) {
            throw new PageNotFoundException(HospitalEntity.class, index, size);
        }
        return hospitalEntityPage.map(hospitalConverter::toResponse);
    }

    @Override
    public Page<HospitalResponse> searchHospitalsByKeyword(int index, int size, String keyword) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }
        Pageable pageable = PageRequest.of(index, size);
        Page<HospitalEntity> hospitalEntityPage = hospitalRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
        if (hospitalEntityPage.isEmpty()) {
            throw new PageNotFoundException(HospitalEntity.class, index, size);
        }
        return hospitalEntityPage.map(hospitalConverter::toResponse);
    }
}
