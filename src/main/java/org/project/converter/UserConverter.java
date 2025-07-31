package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.UserEntity;
import org.project.enums.Gender;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dto.PatientDTO;
import org.project.model.dto.StaffDTO;
import org.project.model.dto.UserRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
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

    public UserRegisterDTO toRegisterDTO(PatientDTO patientDTO) {
        UserRegisterDTO userRegisterDTO = Optional.ofNullable(modelMapper.map(patientDTO, UserRegisterDTO.class))
                .orElseThrow(() -> new ErrorMappingException(PatientDTO.class, UserRegisterDTO.class));
        userRegisterDTO.setPatientEntityAddress(patientDTO.getAddress());
        userRegisterDTO.setPatientEntityGender(Gender.valueOf(patientDTO.getGender()));
        userRegisterDTO.setPatientEntityBirthdate(Date.valueOf(patientDTO.getDateOfBirth()));
        userRegisterDTO.setPatientEntityFullName(patientDTO.getFullName());
        return userRegisterDTO;
    }

    public UserEntity toEntity(StaffDTO staffDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(staffDTO.getUserEntityId());
        userEntity.setEmail(staffDTO.getEmail());
        userEntity.setPhoneNumber(staffDTO.getPhoneNumber());
        userEntity.setUserRole(UserRole.STAFF);
        userEntity.setUserStatus(UserStatus.ACTIVE);
        return userEntity;
    }
}
