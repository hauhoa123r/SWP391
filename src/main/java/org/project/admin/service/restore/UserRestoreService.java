package org.project.admin.service.restore;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.admin.entity.User;
import org.project.admin.repository.UserRepository;

import org.project.admin.service.Log.UserLogService;
import org.project.admin.util.RestoreService;
import org.project.admin.enums.AuditAction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRestoreService implements RestoreService<User> {

    private final UserRepository userRepository;
    private final UserLogService userLogService;

    @Override
    @Transactional
    public void restoreById(Long id) {
        User user = userRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
        user.setDeleted(false);
        userRepository.save(user);

        // Ghi log khi restore
        userLogService.logUserAction(user, AuditAction.RESTORE);
    }
}
