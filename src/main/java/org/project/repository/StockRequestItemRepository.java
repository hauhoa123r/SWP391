package org.project.repository;

import org.project.entity.StockRequestItemEntity;
import org.project.entity.StockRequestItemEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface StockRequestItemRepository extends JpaRepository<StockRequestItemEntity, StockRequestItemEntityId> {
    
    List<StockRequestItemEntity> findByProduct_Id(Long productId);
    
    @Query("SELECT i FROM StockRequestItemEntity i WHERE i.expirationDate <= :expiryDate")
    List<StockRequestItemEntity> findExpiringItems(@Param("expiryDate") Date expiryDate);
    
    @Query("SELECT i FROM StockRequestItemEntity i WHERE i.product.id = :productId AND i.expirationDate IS NOT NULL ORDER BY i.expirationDate ASC")
    List<StockRequestItemEntity> findByProductIdOrderByExpirationDateAsc(@Param("productId") Long productId);
    
    @Query("SELECT i FROM StockRequestItemEntity i WHERE i.stockRequest.status = 'COMPLETED' AND i.product.id = :productId ORDER BY i.stockRequest.requestDate DESC")
    List<StockRequestItemEntity> findRecentStockItemsByProductId(@Param("productId") Long productId);
} 