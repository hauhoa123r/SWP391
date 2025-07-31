package org.project.repository;

import org.project.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    
    /**
     * Tìm notifications theo user ID
     */
    List<NotificationEntity> findByUserEntityId(Long userId);
    
    /**
     * Đếm số notifications theo user ID
     */
    Long countByUserEntityId(Long userId);
} 