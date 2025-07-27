package org.project.service.impl;

import org.project.converter.ProductConverter;
import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.project.model.response.MedicineListVResponse;
import org.project.repository.ProductVRepository;
import org.project.service.ProductVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ProductVServiceImpl implements ProductVService {
    @Autowired
    private ProductVRepository productVRepository;
    @Autowired
    private ProductConverter productConverter;

    @Override
    public Page<MedicineListVResponse> getMedicineList(String keyword, Pageable pageable) {
        Page<ProductEntity> productEntities = productVRepository.findByProductTypeAndNameContainingIgnoreCase(ProductType.MEDICINE,keyword,pageable);
        return productEntities.map(productConverter::toMedicineListVResponse);
    }
}
