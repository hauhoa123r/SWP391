package org.project.service;

import org.project.entity.MedicalProductEntity;
import java.util.List;

public interface MedicalProductService {
    MedicalProductEntity save(MedicalProductEntity medicalProduct);
    MedicalProductEntity findById(Long id);
    List<MedicalProductEntity> findAll();
    void deleteById(Long id);
    void updateStock(Long id, Integer quantity);
} 