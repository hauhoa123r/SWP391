package org.project.service;

import org.project.entity.MedicineEntity;

import java.util.List;

public interface MedicineService {
    MedicineEntity save(MedicineEntity medicine);
    MedicineEntity findById(Long id);
    List<MedicineEntity> findAll();
    void deleteById(Long id);
} 