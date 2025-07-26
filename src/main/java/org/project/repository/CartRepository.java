package org.project.repository;

import java.util.List;
import java.util.Optional;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CartRepository extends JpaRepository<CartItemEntity, CartItemEntityId>{
    List<CartItemEntity> findByUserEntityId(Long userId);
    void deleteByUserEntityIdAndProductEntityId(Long userId, Long productId);
    Optional<CartItemEntity> findByUserEntityIdAndProductEntityId(Long userId, Long productId);

    List<CartItemEntity> findByUserEntity_Id(Long userEntityId);
}