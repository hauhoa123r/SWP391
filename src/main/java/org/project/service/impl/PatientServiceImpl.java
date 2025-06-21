package org.project.service.impl;

import org.project.converter.PatientConverter;
import org.project.entity.PatientEntity;
import org.project.model.response.PatientResponse;
import org.project.repository.PatientRepository;
import org.project.service.PatientService;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    //    private final ModelMapper modelMapper;
    private PatientRepository patientRepository;
    private PatientConverter patientConverter;
    private PageUtils<PatientEntity> pageUtils;

    @Autowired
    public void setPatientRepository(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Autowired
    public void setPatientConverter(PatientConverter patientConverter) {
        this.patientConverter = patientConverter;
    }

    @Autowired
    public void setPageUtils(PageUtils<PatientEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Override
    public Page<PatientResponse> getPatientsByUser(Long userId, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<PatientEntity> patientEntityPage = patientRepository.findAllByUserEntityId(userId, pageable);
        pageUtils.validatePage(patientEntityPage, PatientEntity.class);
        return patientEntityPage.map(patientConverter::toResponse);
    }

    @Override
    public Page<PatientResponse> getPatientsByUserAndKeyword(Long userId, String keyword, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<PatientEntity> patientEntityPage = patientRepository.findAllByUserEntityIdAndFullNameContainingIgnoreCase(userId, keyword, pageable);
        pageUtils.validatePage(patientEntityPage, PatientEntity.class);
        return patientEntityPage.map(patientConverter::toResponse);
    }
//    private UserRepository userRepository;

//    @Autowired
//    public PatientServiceImpl(PatientRepository patientRepository, PatientConverter patientConverter, UserRepository userRepository, ModelMapper modelMapper) {
//        this.patientRepository = patientRepository;
//        this.patientConverter = patientConverter;
//        this.userRepository = userRepository;
//        this.modelMapper = modelMapper;
//    }
//
//    @Override
//    public void createPatient(PatientDTO patientDTO) {
//        PatientEntity patientEntity = new PatientEntity();
//
//        patientEntity = patientConverter.toConvertEntity(patientDTO).orElse(patientEntity);
//
//        Optional<UserEntity> userEntityOptional = userRepository.findById(patientDTO.getUserId());
//        patientEntity.setUserEntity(userEntityOptional.orElse(null));
//
//        patientRepository.save(patientEntity);
//    }
//
//    @Override
//    public List<PatientResponse> getAllPatients() {
//        List<PatientEntity> patientEntities = patientRepository.findAll();
//        return patientEntities.stream()
//                .map(patientConverter::toConvertResponse)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .toList();
//    }
//
//    @Override
//    public Optional<PatientResponse> getPatientById(Long patientId) {
//        Optional<PatientEntity> patientEntityOptional = patientRepository.findById(patientId);
//        return patientEntityOptional.flatMap(patientConverter::toConvertResponse);
//    }
//
//    @Override
//    public void updatePatient(Long patientId, PatientDTO patientDTO) {
//        Optional<PatientEntity> patientEntityOptional = patientRepository.findById(patientId);
//        if (patientEntityOptional.isPresent()) {
//            PatientEntity patientEntity = patientEntityOptional.get();
//
//            modelMapper.map(patientDTO, patientEntity);
//
//            Optional<UserEntity> userEntityOptional = userRepository.findById(patientDTO.getUserId());
//            patientEntity.setUserEntity(userEntityOptional.orElse(null));
//
//            patientRepository.save(patientEntity);
//        } else {
//            throw new ResourceNotFoundException("Patient not found");
//        }
//    }
//
//    @Override
//    public void deletePatient(Long patientId) {
//        Optional<PatientEntity> patientEntityOptional = patientRepository.findById(patientId);
//        if (patientEntityOptional.isPresent()) {
//            patientRepository.delete(patientEntityOptional.get());
//        } else {
//            throw new ResourceNotFoundException("Patient not found");
//        }
//    }
}
