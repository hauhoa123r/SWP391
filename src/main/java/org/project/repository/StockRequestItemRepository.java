package org.project.repository;

import org.project.entity.StockRequestItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface StockRequestItemRepository extends JpaRepository<StockRequestItemEntity, Long> {
    List<StockRequestItemEntity> findByStockRequestId(Long requestId);
    List<StockRequestItemEntity> findByProductId(Long productId);
    List<StockRequestItemEntity> findByExpirationDateBefore(Date date);
} 