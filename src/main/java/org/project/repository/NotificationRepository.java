package org.project.repository;

import org.project.entity.NotificationEntity;
import org.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserEntity_IdOrderByCreatedAtDesc(Long userId);
}


