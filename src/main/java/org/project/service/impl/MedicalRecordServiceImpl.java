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
}
