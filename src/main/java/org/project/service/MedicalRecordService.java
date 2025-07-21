package org.project.service;

public interface MedicalRecordService {
    String getMainReason(Long appointmentId);
    String getDiagnosis(Long appointmentId);
    String getPlan(Long appointmentId);
    boolean addMainReason(Long appointmentId, String diagnosis);
    boolean addDiagnosis(Long appointmentId, String diagnosis);
    boolean addPlan(Long appointmentId, String plan);
}
