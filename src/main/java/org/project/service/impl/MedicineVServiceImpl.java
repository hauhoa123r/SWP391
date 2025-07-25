package org.project.service.impl;

import org.project.entity.MedicineEntity;
import org.project.model.response.MedicineListVResponse;
import org.project.repository.MedicineVRepository;
import org.project.service.MedicineVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MedicineVServiceImpl implements MedicineVService {
    @Autowired
    private MedicineVRepository medicineVRepository;
    @Override
    public List<MedicineListVResponse> getMedicineList() {
        List<MedicineEntity> list = medicineVRepository.findAll();
        return List.of();
    }
}
