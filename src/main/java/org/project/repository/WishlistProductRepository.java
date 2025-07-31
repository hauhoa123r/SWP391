package org.project.repository;

import org.project.entity.WishlistProductEntity;
import org.project.entity.WishlistProductEntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistProductRepository extends JpaRepository<WishlistProductEntity, WishlistProductEntityId> {
    Logger log = LoggerFactory.getLogger(WishlistProductRepository.class);
    
    List<WishlistProductEntity> findByIdUserId(Long userId);

    void deleteByIdUserIdAndIdProductId(Long userId, Long productId);
    
    default void logDeleteOperation(Long userId, Long productId) {
        log.info("Repository: Attempting to delete product {} for user {}", productId, userId);
    }
    
    default void logExistsOperation(Long userId, Long productId, boolean exists) {
        log.info("Repository: Product {} for user {} exists: {}", productId, userId, exists);
    }
}
