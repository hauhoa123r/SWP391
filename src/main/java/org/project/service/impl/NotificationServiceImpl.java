package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.NotificationEntity;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.repository.NotificationRepository;
import org.project.repository.PatientRepository;
import org.project.repository.UserRepository;
import org.project.service.EmailService;
import org.project.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final EmailService emailService;

    // Trả danh sách thông báo của user (dùng cho giao diện web)
    @Override
    public List<NotificationEntity> getAllByUserId(Long userId) {
        return notificationRepository.findByUserEntity_IdOrderByCreatedAtDesc(userId);
    }

    // Tạo một thông báo trong DB (hiện trên web)
    @Override
    public NotificationEntity send(UserEntity user, String title, String content) {
        NotificationEntity notification = NotificationEntity.builder()
                .userEntity(user)
                .content(content)
                .build();
        return notificationRepository.save(notification);
    }

    // Gửi thông báo ( email)
    @Override
    public void sendNotification(Long userId, String title, String content) {
        // Lấy user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lưu thông báo cho web
        send(user, title, content);

        // Tìm tất cả bệnh nhân mà user đang sở hữu
        List<PatientEntity> patientList = patientRepository.findByUserEntity_Id(userId);

        // Gửi email cho chính user
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            emailService.sendEmail(user.getEmail(), title, content);
        }

        // Gửi email đến từng bệnh nhân (nếu có email)
        for (PatientEntity patient : patientList) {
            if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
                emailService.sendEmail(patient.getEmail(), title, content);
            }
        }
    }





}
