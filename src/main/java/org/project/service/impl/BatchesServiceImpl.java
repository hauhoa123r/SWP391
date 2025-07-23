package org.project.service.impl;

import org.project.entity.Batch;
import org.project.entity.MedicineEntity;
import org.project.model.dto.BatchDTO;
import org.project.repository.BatchesRepository;
import org.project.service.BatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BatchesServiceImpl implements BatchesService {
    //inject Batches Repo
    @Autowired
    private BatchesRepository batchesRepository;

    @Override
    public Set<Batch> findByMedicine(MedicineEntity medicine) {
        return batchesRepository.findByMedicine(medicine);
    }

    @Override
    public Set<BatchDTO> findDTOByMedicine(MedicineEntity medicine) {
        return findByMedicine(medicine).stream().map(this::convertToDTO).collect(Collectors.toSet());
    }

    //convert to DTO
    private BatchDTO convertToDTO(Batch batch) {
        BatchDTO batchDTO = new BatchDTO();
        //set id
        batchDTO.setId(batch.getId());
        //set batchCode
        batchDTO.setBatchCode(batch.getBatchCode());
        //set quantity
        batchDTO.setQuantity(batch.getQuantity());
        //set manufactureDate
        batchDTO.setManufactureDate(batch.getManufactureDate());
        //set expire date
        batchDTO.setExpiryDate(batch.getExpiryDate());
        return batchDTO;
    }
}
