package org.project.repository;

import org.project.entity.WishlistProductEntity;
import org.project.entity.WishlistProductEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistProductRepository extends JpaRepository<WishlistProductEntity, WishlistProductEntityId> {
    List<WishlistProductEntity> findByIdUserId(Long userId);

    void deleteByIdUserIdAndIdProductId(Long userId, Long productId);
}
