package org.project.converter;

import org.project.entity.ProductEntity;
import org.project.model.dto.DoctorMedicineDTO;
import org.project.model.response.DoctorMedicineResponse;
import org.project.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DoctorMedicineConverter {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Page<DoctorMedicineResponse> toFilterMedicine(DoctorMedicineDTO doctorMedicineDTO){
        Page<ProductEntity> productEntities = appointmentRepository.toFilterMedicine(doctorMedicineDTO);
        Page<DoctorMedicineResponse> result = productEntities.map(entity ->{
           DoctorMedicineResponse doctorMedicineResponse = new DoctorMedicineResponse();
            doctorMedicineResponse.setMedicineName(entity.getName());
            doctorMedicineResponse.setPrice(entity.getPrice().longValue());
            doctorMedicineResponse.setMedicineId(entity.getId());
            return doctorMedicineResponse;
        });

        return result;
    }
}
