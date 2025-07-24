package org.project.admin.service.restore;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.admin.entity.Staff;
import org.project.admin.repository.StaffRepository;

import org.project.admin.service.Log.StaffLogService;
import org.project.admin.util.RestoreService;
import org.project.admin.enums.AuditAction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffRestoreService implements RestoreService<Staff> {

    private final StaffRepository staffRepository;
    private final StaffLogService staffLogService;  // Service ghi log

    @Override
    @Transactional
    public void restoreById(Long id) {
        Staff staff = staffRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Staff"));
        staff.setDeleted(false);
        staffRepository.save(staff);

        // Ghi log khi restore
        staffLogService.logStaffAction(staff, AuditAction.RESTORE);  // Ghi log hành động restore
    }
}
