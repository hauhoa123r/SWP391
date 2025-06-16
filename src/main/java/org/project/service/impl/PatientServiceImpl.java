package org.project.service.impl;

import org.modelmapper.ModelMapper;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.enums.BloodType;
import org.project.enums.FamilyRelationship;
import org.project.enums.Gender;
import org.project.enums.converter.BloodTypeConverter;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.PatientDTO;
import org.project.model.response.PatientResponse;
import org.project.repository.PatientRepository;
import org.project.repository.UserRepository;
import org.project.service.PatientService;
import org.project.converter.PatientConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    private final ModelMapper modelMapper;
    private final PatientRepository patientRepository;
    private final PatientConverter patientConverter;
    private final UserRepository userRepository;
    private final BloodTypeConverter bloodTypeConverter = new BloodTypeConverter();
    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, PatientConverter patientConverter, UserRepository userRepository, ModelMapper modelMapper) {
        this.patientRepository = patientRepository;
        this.patientConverter = patientConverter;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public Long createPatient(PatientDTO patientDTO) {
        PatientEntity patientEntity = patientConverter.toConvertEntity(patientDTO)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient DTO"));

        patientEntity.setBirthdate(Date.valueOf(patientDTO.getDateOfBirth()));
        patientEntity.setRelationship(FamilyRelationship.valueOf(patientDTO.getFamilyRelationship().toUpperCase()));
        patientEntity.setGender(Gender.valueOf(patientDTO.getGender().toUpperCase()));
        patientEntity.setBloodType(BloodType.valueOf(patientDTO.getBloodType().toUpperCase()));
        patientEntity.setAvatarUrl(patientDTO.getAvatarBase64());

        UserEntity userEntity = userRepository.findById(patientDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        patientEntity.setUserEntity(userEntity);

        userEntity.addPatientEntity(patientEntity);

        PatientEntity savedEntity =  patientRepository.save(patientEntity);
        Long patientId = savedEntity.getId();
        return patientId;
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        List<PatientEntity> patientEntities = patientRepository.findAll();
        if (patientEntities.isEmpty()) {
            throw new ResourceNotFoundException("No patients found");
        }

        return patientEntities.stream()
                .map(entity -> {
                    PatientResponse response = patientConverter.toConvertResponse(entity);

                    // Xử lý ngày tháng an toàn
                    if (entity.getBirthdate() != null) {
                        response.setDateOfBirth(entity.getBirthdate().toString());
                    } else {
                        response.setDateOfBirth("N/A");
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientResponse> getAllPatientsByUserId(Long userId) {
        List<PatientEntity> patientEntities = patientRepository.findAllByUserEntity_Id(userId);
        if (patientEntities.isEmpty()) {
            throw new ResourceNotFoundException("No patients found for user with ID: " + userId);
        }

        return patientEntities.stream()
                .map(entity -> {
                    PatientResponse response = patientConverter.toConvertResponse(entity);

                    // Xử lý ngày tháng an toàn
                    if (entity.getBirthdate() != null) {
                        response.setDateOfBirth(entity.getBirthdate().toString());
                    } else {
                        response.setDateOfBirth("N/A");
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientResponse> getAllPatientsByUserIdPaged(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientEntity> patientPage = patientRepository.findAllByUserEntity_Id(userId, pageable);
        if (patientPage.isEmpty()) {
            throw new ResourceNotFoundException("No patients found for user with ID: " + userId);
        }
        List<PatientEntity> patientEntities = patientPage.getContent();
        return patientEntities.stream()
                .map(entity -> {
                    PatientResponse response = patientConverter.toConvertResponse(entity);

                    // Xử lý ngày tháng an toàn
                    if (entity.getBirthdate() != null) {
                        response.setDateOfBirth(entity.getBirthdate().toString());
                    } else {
                        response.setDateOfBirth("N/A");
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponse getPatientById(Long patientId) {
        PatientEntity patientEntity = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        PatientResponse response = patientConverter.toConvertResponse(patientEntity);
        if(patientEntity.getBirthdate() != null) {
            response.setDateOfBirth(patientEntity.getBirthdate().toString());
        } else {
            response.setDateOfBirth("N/A");
        }
        return response;
    }

    @Override
    public void updatePatient(Long patientId, PatientDTO patientDTO) {
        Optional<PatientEntity> patientEntityOptional = patientRepository.findById(patientId);
        if (patientEntityOptional.isPresent()) {
            PatientEntity patientEntity = patientEntityOptional.get();

            modelMapper.map(patientDTO, patientEntity);

            Optional<UserEntity> userEntityOptional = userRepository.findById(patientDTO.getUserId());
            patientEntity.setUserEntity(userEntityOptional.orElse(null));

            patientRepository.save(patientEntity);
        } else {
            throw new ResourceNotFoundException("Patient not found");
        }
    }

    @Override
    public void deletePatient(Long patientId) {
        Optional<PatientEntity> patientEntityOptional = patientRepository.findById(patientId);
        if (patientEntityOptional.isPresent()) {
            patientRepository.delete(patientEntityOptional.get());
        } else {
            throw new ResourceNotFoundException("Patient not found");
        }
    }

    @Override
    public List<String> getAllRelationships(Long userId) {
        List<String> relationships = patientRepository.getAllRelationships(userId)
                .stream()
                .map(FamilyRelationship::name)
                .collect(Collectors.toList());
        if (relationships.isEmpty()) {
            throw new ResourceNotFoundException("No relationships found for user with ID: " + userId);
        } else {
            return relationships;
        }
    }

    @Override
    public Long getPatientIdByUserId(Long userId) {
        return patientRepository.findFirstByUserEntity_IdOrderByIdDesc(userId);
    }
}
