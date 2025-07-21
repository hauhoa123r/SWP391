package org.project.service.impl;

import org.project.converter.VitalSignConverter;
import org.project.entity.MedicalRecordEntity;
import org.project.entity.VitalSignEntity;
import org.project.model.request.AddVitalSignRequest;
import org.project.model.response.VitalSignResponse;
import org.project.repository.MedicalRecordRepository;
import org.project.repository.VitalSignRepository;
import org.project.service.VitalSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
public class VitalSignServiceImpl implements VitalSignService {
    @Autowired
    private VitalSignRepository vitalSignRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private VitalSignConverter vitalSignConverter;
    @Override
    public boolean addVitalSign(Long medicalRecordId, AddVitalSignRequest request) {
        MedicalRecordEntity  medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        VitalSignEntity vitalSign = VitalSignEntity.builder()
                .medicalRecord(medicalRecord)
                .pulseRate(request.getPulseRate())
                .bpSystolic(request.getBpSystolic())
                .bpDiastolic(request.getBpDiastolic())
                .temperature(request.getTemperature())
                .respiratoryRate(request.getRespiratoryRate())
                .spo2(request.getSpo2())
                .recordedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        vitalSignRepository.save(vitalSign);
        return true;
    }

    @Override
    public boolean updateVitalSign(Long vitalSignId, AddVitalSignRequest request) {
        VitalSignEntity vitalSign = vitalSignRepository.findById(vitalSignId).orElse(null);
        if(vitalSign != null) {
            vitalSign.setPulseRate(request.getPulseRate());
            vitalSign.setBpSystolic(request.getBpSystolic());
            vitalSign.setBpDiastolic(request.getBpDiastolic());
            vitalSign.setTemperature(request.getTemperature());
            vitalSign.setRespiratoryRate(request.getRespiratoryRate());
            vitalSign.setSpo2(request.getSpo2());
            vitalSign.setRecordedAt(new Timestamp(System.currentTimeMillis()));
            vitalSignRepository.save(vitalSign);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteVitalSign(Long vitalSignId) {
        VitalSignEntity vitalSign = vitalSignRepository.findById(vitalSignId).orElse(null);
        if(vitalSign != null) {
            vitalSignRepository.delete(vitalSign);
            return true;
        }
        return false;
    }

    @Override
    public List<VitalSignResponse> getVitalSign(Long medicalRecordId) {
        List<VitalSignEntity> vitalSignEntities = vitalSignRepository.findByMedicalRecordId(medicalRecordId);
        return vitalSignEntities.stream().map(vitalSignConverter::toVitalSignResponse).toList();
    }
}
