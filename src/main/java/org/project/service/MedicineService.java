package org.project.service;

import org.project.entity.MedicineEntity;
import org.project.enums.operation.SortDirection;
import org.project.model.dto.MedicineDTO;
import org.project.model.dto.SupplierInDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MedicineService {
    MedicineEntity save(MedicineEntity medicine);
    MedicineEntity findById(Long id);
    List<MedicineEntity> findAll();
    void deleteById(Long id);
    
    // Additional methods needed by MedicineController
    Page<MedicineDTO> getAllMedicines(int page, int size, String name, SortDirection sortDirection, String sortField);
    MedicineDTO createMedicine(MedicineDTO medicineDTO);
    MedicineDTO updateMedicine(MedicineDTO medicineDTO);
    void processSupplierIn(SupplierInDTO supplierInDTO);

}