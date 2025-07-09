package org.project.repository;

import org.project.entity.OrderItemEntity;
import org.project.entity.OrderItemEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, OrderItemEntityId> {
    List<OrderItemEntity> findByOrderEntity_Id(Long orderId);
    List<OrderItemEntity> findByProductEntity_Id(Long productId);
    Optional<OrderItemEntity> findById(OrderItemEntityId id);
} 