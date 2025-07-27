package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.ProductEntity;
import org.project.model.response.MedicineListVResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {
    @Autowired
    private ModelMapper modelMapper;

    public MedicineListVResponse toMedicineListVResponse(ProductEntity productEntity) {
        return modelMapper.map(productEntity, MedicineListVResponse.class);
    }
}
