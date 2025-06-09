package org.project.ai.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.PatientEntity;
import org.project.model.dai.DataUserDAI;
import org.project.repository.MedicalProfileRepository;
import org.project.repository.PatientRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class DataPatientConverter {
    private final MedicalProfileRepository medicalProfileRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    public DataPatientConverter(MedicalProfileRepository medicalProfileRepository, ModelMapper modelMapper,
                                PatientRepository patientRepository) {
        this.medicalProfileRepository = medicalProfileRepository;
        this.modelMapper = modelMapper;
        this.patientRepository = patientRepository;
    }
    private String dataResult(DataUserDAI dataUserDAI){
        return """
                Đây là thông tin của người dùng:
                - Tên: %s
                - Giới tính: %s
                - Loại máu: %s
                - Dị ứng: %s
                - Bệnh án: %s
                """.formatted(dataUserDAI.getFullName(),
                dataUserDAI.getGender(),
                dataUserDAI.getBloodType(),
                dataUserDAI.getAllergies(),
                dataUserDAI.getChronicDiseases());
    }

    public String toConverterDataUser(Long patientId){
        Optional<PatientEntity> patientOpt = patientRepository.findById(patientId);
        PatientEntity patient = new PatientEntity();
        if (patientOpt.isPresent()) {
            patient = patientOpt.get();
        }
        DataUserDAI dataUserDAI = modelMapper.map(patient, DataUserDAI.class);

        dataUserDAI.setAllergies(patient.getMedicalProfile().get(0).getAllergies());
        dataUserDAI.setBloodType(patient.getMedicalProfile().get(0).getBloodType());
        dataUserDAI.setChronicDiseases(patient.getMedicalProfile().get(0).getChronicDiseases());

        return dataResult(dataUserDAI);
    }
}
