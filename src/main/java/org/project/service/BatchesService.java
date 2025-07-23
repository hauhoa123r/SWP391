package org.project.service;

import org.project.entity.Batch;
import org.project.entity.MedicineEntity;
import org.project.model.dto.BatchDTO;

import java.util.Set;

public interface BatchesService {
    //find batch by medicine
    Set<Batch> findByMedicine(MedicineEntity medicine);
    //find batch dto by medicine
    Set<BatchDTO> findDTOByMedicine(MedicineEntity medicine);
}
