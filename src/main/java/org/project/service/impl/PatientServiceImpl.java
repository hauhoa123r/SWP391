package org.project.service.impl;

import org.modelmapper.ModelMapper;
import org.project.config.WebConstant;
import org.project.converter.PatientConverter;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.enums.BloodType;
import org.project.enums.FamilyRelationship;
import org.project.enums.Gender;
import org.project.enums.PatientStatus;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.PatientDTO;
import org.project.model.response.PatientResponse;
import org.project.repository.PatientRepository;
import org.project.repository.UserRepository;
import org.project.service.PatientService;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private static final String AVATAR_DIR_RELATIVE =
            Paths.get("src", "main", "resources",
                            "templates", "frontend", "assets",
                            "images", "patientAvatar")
                    .toString();
    //    private final ModelMapper modelMapper;
    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private PatientConverter patientConverter;
    private PageUtils<PatientEntity> pageUtils;

    @Autowired
    public void setPatientRepository(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
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
        Page<PatientEntity> patientEntityPage = patientRepository.findAllByUserEntityIdAndPatientStatus(userId, pageable, WebConstant.PATIENT_STATUS_ACTIVE);
        return patientEntityPage.map(patientConverter::toResponse);
    }

    @Override
    public Page<PatientResponse> getPatientsByUserAndKeyword(Long userId, String keyword, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<PatientEntity> patientEntityPage = patientRepository.findAllByUserEntityIdAndFullNameContainingIgnoreCase(userId, keyword, pageable);
        pageUtils.validatePage(patientEntityPage, PatientEntity.class);
        return patientEntityPage.map(patientConverter::toResponse);
    }

    @Transactional
    @Override
    public Long createPatient(PatientDTO patientDTO) {
        PatientEntity patientEntity = patientConverter.toConvertEntity(patientDTO)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient DTO"));

        patientEntity.setBirthdate(Date.valueOf(patientDTO.getDateOfBirth()));
        patientEntity.setFamilyRelationship(FamilyRelationship.valueOf(patientDTO.getFamilyRelationship().toUpperCase()));
        patientEntity.setGender(Gender.valueOf(patientDTO.getGender().toUpperCase()));
        if (patientDTO.getBloodType() != null) {
            patientEntity.setBloodType(BloodType.valueOf(patientDTO.getBloodType().toUpperCase()));
        }
        if (patientDTO.getAvatarBase64() != null && !patientDTO.getAvatarBase64().isEmpty()) {
            patientEntity.setAvatarUrl(toConvertAvatarUrl(patientDTO.getAvatarBase64()));
        }

        UserEntity userEntity = userRepository.findById(patientDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        patientEntity.setUserEntity(userEntity);

        userEntity.addPatientEntity(patientEntity);

        PatientEntity savedEntity = patientRepository.save(patientEntity);
        Long patientId = savedEntity.getId();
        return patientId;
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        List<PatientEntity> patientEntities = patientRepository.findAll();
        if (patientEntities.isEmpty()) {
            throw new ResourceNotFoundException("No patients found");
        }

        List<PatientResponse> patientResponses = patientEntities.stream()
                .map(patientConverter::toConvertResponse)
                .collect(Collectors.toList());

        for (PatientResponse patientResponse : patientResponses) {
            if (patientResponse.getAvatarUrl() != null) {
                patientResponse.setAvatarUrl(toConvertFileToBase64(patientResponse.getAvatarUrl()));
            } else {
                patientResponse.setAvatarUrl(null);
            }
        }
        return patientResponses;

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

                    if (entity.getBirthdate() != null) {
                        response.setBirthdate(entity.getBirthdate().toString());
                    } else {
                        response.setBirthdate("N/A");
                    }
                    if (entity.getAvatarUrl() != null) {
                        response.setAvatarUrl(toConvertFileToBase64(entity.getAvatarUrl()));
                    } else {
                        response.setAvatarUrl(null);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PatientResponse getPatientById(Long patientId) {
        PatientEntity patientEntity = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        PatientResponse response = patientConverter.toConvertResponse(patientEntity);

        if (response.getBirthdate() != null) {
            response.setBirthdate(patientEntity.getBirthdate().toString());
        } else {
            response.setBirthdate("N/A");
        }
        if (patientEntity.getAvatarUrl() != null) {
            response.setAvatarUrl(toConvertFileToBase64(patientEntity.getAvatarUrl()));
        } else {
            response.setAvatarUrl(null);
        }

        return response;
    }

    @Transactional
    @Override
    public PatientResponse updatePatient(Long patientId, PatientDTO patientDTO) {
        PatientEntity patientEntity = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        Method[] methods = PatientDTO.class.getMethods();

        for (Method method : methods) {
            String name = method.getName();

            if ("getDateOfBirth".equals(name) && method.getParameterCount() == 0) {
                try {
                    String dobStr = (String) method.invoke(patientDTO);
                    if (dobStr != null && !dobStr.isEmpty()) {
                        Date dateOfBirth = Date.valueOf(LocalDate.parse(dobStr, DateTimeFormatter.ISO_DATE));
                        patientEntity.setBirthdate(dateOfBirth);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error updating date of birth: " + e.getMessage(), e);
                }
                continue;
            }
            if ("getAvatarBase64".equals(name) && method.getParameterCount() == 0) {
                try {
                    String avatarBase64 = (String) method.invoke(patientDTO);
                    if (avatarBase64 != null && !avatarBase64.isEmpty()) {
                        String avatarUrl = toConvertAvatarUrl(avatarBase64);
                        patientEntity.setAvatarUrl(avatarUrl);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error updating avatar: " + e.getMessage(), e);
                }
                continue;
            }

            if (name.startsWith("get") && method.getParameterCount() == 0) {
                try {
                    Object value = method.invoke(patientDTO);
                    if (value != null) {
                        String setterName = "set" + name.substring(3);
                        Method setterMethod = PatientEntity.class.getMethod(setterName, method.getReturnType());
                        setterMethod.invoke(patientEntity, value);
                    }
                } catch (NoSuchMethodException e) {

                } catch (Exception e) {
                    throw new RuntimeException("Error updating patient: " + e.getMessage(), e);
                }
            }
        }

        if (patientDTO.getFamilyRelationship() != null) {
            patientEntity.setFamilyRelationship(
                    FamilyRelationship.valueOf(patientDTO.getFamilyRelationship().toUpperCase())
            );
        }

        if (patientDTO.getGender() != null) {
            patientEntity.setGender(
                    Gender.valueOf(patientDTO.getGender().toUpperCase())
            );
        }

        if (patientDTO.getBloodType() != null) {
            patientEntity.setBloodType(
                    BloodType.valueOf(patientDTO.getBloodType().toUpperCase())
            );
        }

        userRepository.findById(patientDTO.getUserId())
                .ifPresentOrElse(patientEntity::setUserEntity,
                        () -> {
                            throw new ResourceNotFoundException("User not found with ID: " + patientDTO.getUserId());
                        });

        patientRepository.save(patientEntity);

        PatientResponse patientResponse = getPatientById(patientId);

        return patientResponse;
    }

    @Override
    public void deletePatient(Long patientId) {
        Optional<PatientEntity> patientEntityOptional = patientRepository.findById(patientId);
        if (patientEntityOptional.isPresent()) {
            PatientEntity patientEntity = patientEntityOptional.get();
            patientEntity.setPatientStatus(PatientStatus.INACTIVE);
            patientRepository.save(patientEntity);
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

    public String toConvertAvatarUrl(String avatarBase64) {
        if (avatarBase64 == null || avatarBase64.isEmpty()) {
            return null;
        }
        try {
            String extension = "png";
            String base64Data = avatarBase64;
            if (avatarBase64.contains(",")) {
                String[] parts = avatarBase64.split(",");
                String meta = parts[0];
                base64Data = parts[1];
                if (meta.contains("image/")) {
                    extension = meta.substring(meta.indexOf(",") + 6, meta.indexOf(";"));
                }
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            String fileName = UUID.randomUUID() + "." + extension;

            Path avatarDir = Paths.get(System.getProperty("user.dir"), AVATAR_DIR_RELATIVE);
            File dir = avatarDir.toFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(imageBytes);
            }
            return "templates/frontend/assets/images/patientAvatar/" + fileName;
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    public String toConvertFileToBase64(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return null;
        }
        try {
            ClassPathResource imgFile = new ClassPathResource(avatarUrl);
            if (!imgFile.exists()) {
                return null;
            }
            byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
            String avatarBase64 = Base64.getEncoder().encodeToString(bytes);
            return avatarBase64;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public UserEntity getUserHasPatient() {
        return patientRepository.getRandom().getUserEntity();
    }
}