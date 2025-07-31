package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.ProductAdditionalInfoEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dto.ProductAdditionalInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductAdditionalInfoConverter {
    private ModelMapper modelMapper;

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ProductAdditionalInfoEntity toEntity(ProductAdditionalInfoDTO productAdditionalInfoDTO) {
        return Optional.ofNullable(
                modelMapper.map(productAdditionalInfoDTO, ProductAdditionalInfoEntity.class)
        ).orElseThrow(() -> new ErrorMappingException(
                ProductAdditionalInfoEntity.class,
                ProductAdditionalInfoDTO.class
        ));
    }
}
