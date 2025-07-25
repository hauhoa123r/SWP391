package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.UserEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dto.UserRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserConverter {
    private ModelMapper modelMapper;

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserEntity toEntity(UserRegisterDTO userRegisterDTO) {
        return Optional.ofNullable(modelMapper.map(userRegisterDTO, UserEntity.class))
                .orElseThrow(() -> new ErrorMappingException(UserRegisterDTO.class, UserEntity.class));
    }
}
