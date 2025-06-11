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
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    private final ModelMapper modelMapper;
    private  PatientRepository patientRepository;
    private PatientConverter patientConverter;
    private UserRepository userRepository;
    private BloodTypeConverter bloodTypeConverter = new BloodTypeConverter();
    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, PatientConverter patientConverter, UserRepository userRepository, ModelMapper modelMapper) {
        this.patientRepository = patientRepository;
        this.patientConverter = patientConverter;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createPatient(PatientDTO patientDTO) {
        PatientEntity patientEntity = new PatientEntity();
        Date birthDate = Date.valueOf(patientDTO.getDateOfBirth());
        FamilyRelationship familyRelationship = FamilyRelationship.valueOf(patientDTO.getFamilyRelationship().toUpperCase());
        Gender gender = Gender.valueOf(patientDTO.getGender().toUpperCase());
        BloodType bloodType = BloodType.valueOf(patientDTO.getBloodType().toUpperCase());
        Long patientId = null;
        patientEntity = patientConverter.toConvertEntity(patientDTO).orElse(patientEntity);
        patientEntity.setBirthdate(birthDate);
        patientEntity.setRelationship(familyRelationship);
        patientEntity.setGender(gender);
        patientEntity.setBloodType(bloodType);

        Optional<UserEntity> userEntityOptional = userRepository.findById(patientDTO.getUserId());
        patientEntity.setUserEntity(userEntityOptional.orElse(null));
        patientEntity.setId(patientId);
        patientRepository.save(patientEntity);
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        List<PatientEntity> patientEntities = patientRepository.findAll();
        return patientEntities.stream()
                .map(patientConverter::toConvertResponse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public Optional<PatientResponse> getPatientById(Long patientId) {
        Optional<PatientEntity> patientEntityOptional = patientRepository.findById(patientId);
        return patientEntityOptional.flatMap(patientConverter::toConvertResponse);
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
}
