package org.project.repository.impl;

import org.project.entity.ProductEntity;
import org.project.model.dto.DoctorMedicineDTO;
import org.springframework.data.domain.Page;

public interface DoctorMedicineRepositoryCustom {
    Page<ProductEntity> toFilterMedicine(DoctorMedicineDTO doctorMedicineDTO);
}
