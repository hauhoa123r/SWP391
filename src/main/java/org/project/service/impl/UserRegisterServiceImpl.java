package org.project.service.impl;

import org.project.converter.PatientConverter;
import org.project.converter.UserConverter;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.enums.PatientStatus;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.project.exception.ErrorResponse;
import org.project.model.dto.UserRegisterDTO;
import org.project.repository.PatientRepository;
import org.project.repository.UserRepository;
import org.project.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserRegisterServiceImpl implements UserRegisterService {
    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private UserConverter userConverter;
    private PatientConverter patientConverter;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPatientRepository(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Autowired
    public void setUserConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    @Autowired
    public void setPatientConverter(PatientConverter patientConverter) {
        this.patientConverter = patientConverter;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        // Validate duplicate email
        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            throw new ErrorResponse("Email đã được sử dụng");
        }
        // Validate duplicate phone number
        if (userRepository.existsByPhoneNumber(userRegisterDTO.getPhoneNumber())) {
            throw new ErrorResponse("Số điện thoại đã được sử dụng");
        }

        UserEntity userEntity = userConverter.toEntity(userRegisterDTO);
        userEntity.setPasswordHash(passwordEncoder.encode(userRegisterDTO.getPassword()));
        userEntity.setUserStatus(UserStatus.ACTIVE);
        userEntity.setUserRole(UserRole.PATIENT);
        userEntity = userRepository.save(userEntity);

        PatientEntity patientEntity = patientConverter.toEntity(userRegisterDTO);
        patientEntity.setUserEntity(userEntity);
        patientEntity.setPatientStatus(PatientStatus.ACTIVE);
        patientRepository.save(patientEntity);
    }
}
