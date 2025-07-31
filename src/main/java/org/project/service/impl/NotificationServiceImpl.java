package org.project.service.impl;

import org.project.entity.NotificationEntity;
import org.project.model.response.NotificationResponse;
import org.project.repository.NotificationRepository;
import org.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @Override
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        List<NotificationEntity> notifications = notificationRepository.findByUserEntityId(userId);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Long getNotificationCount(Long userId) {
        return notificationRepository.countByUserEntityId(userId);
    }
    
    /**
     * Convert NotificationEntity to NotificationResponse
     */
    private NotificationResponse convertToResponse(NotificationEntity entity) {
        return new NotificationResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getUserEntity().getId()
        );
    }
} 