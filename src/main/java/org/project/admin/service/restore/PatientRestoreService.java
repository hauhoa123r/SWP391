package org.project.admin.service.restore;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.admin.entity.Patient;
import org.project.admin.repository.PatientRepository;
import org.project.admin.service.Log.PatientLogService;
import org.project.admin.util.RestoreService;
import org.project.admin.enums.AuditAction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientRestoreService implements RestoreService<Patient> {

    private final PatientRepository patientRepository;
    private final PatientLogService patientLogService;  // Service ghi log

    @Override
    @Transactional
    public void restoreById(Long id) {
        Patient patient = patientRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Patient"));
        patient.setDeleted(false);
        patientRepository.save(patient);

        // Ghi log khi restore
        patientLogService.logPatientAction(patient, AuditAction.RESTORE);  // Ghi log hành động restore
    }
}
