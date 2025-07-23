package org.project.repository;

import org.project.entity.SupplierTransactionItemEntity;
import org.project.entity.SupplierTransactionItemEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierTransactionItemRepository extends JpaRepository<SupplierTransactionItemEntity, SupplierTransactionItemEntityId> {
    List<SupplierTransactionItemEntity> findBySupplierTransactionEntityId(Long transactionId);
    List<SupplierTransactionItemEntity> findByIdProductId(Long productId);
}