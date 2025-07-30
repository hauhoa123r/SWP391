package org.project.service;

import org.project.model.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    
    /**
     * Lấy danh sách notifications theo user ID
     */
    List<NotificationResponse> getNotificationsByUser(Long userId);
    
    /**
     * Lấy số lượng notifications theo user ID
     */
    Long getNotificationCount(Long userId);
} 