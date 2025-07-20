package org.project.service.impl;

import org.project.entity.MedicalRecordEntity;
import org.project.repository.MedicalRecordRepository;
import org.project.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {
    @Autowired
    MedicalRecordRepository medicalRecordRepository;

    @Override
    public String getMainReason(Long appointmentId) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findMedicalRecordEntityByAppointmentEntityId(appointmentId);
        return medicalRecord.getMainComplaint();
    }

    @Override
    public String getDiagnosis(Long appointmentId) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findMedicalRecordEntityByAppointmentEntityId(appointmentId);
        return medicalRecord.getDiagnosis();
    }

    @Override
    public String getPlan(Long appointmentId) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findMedicalRecordEntityByAppointmentEntityId(appointmentId);
        return medicalRecord.getTreatmentPlan();
    }

    @Override
    public boolean addMainReason(Long appointmentId, String diagnosis) {
        return false;
    }

    @Override
    public boolean addDiagnosis(Long appointmentId, String diagnosis) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findMedicalRecordEntityByAppointmentEntityId(appointmentId);
        medicalRecord.setDiagnosis(diagnosis);
        medicalRecordRepository.save(medicalRecord);
        return true;
    }

    @Override
    public boolean addPlan(Long appointmentId, String plan) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findMedicalRecordEntityByAppointmentEntityId(appointmentId);
        medicalRecord.setTreatmentPlan(plan);
        medicalRecordRepository.save(medicalRecord);
        return true;
    }
}
