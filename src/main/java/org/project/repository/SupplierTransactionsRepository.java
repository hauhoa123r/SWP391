package org.project.repository;

import org.project.entity.ProductEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.springframework.data.domain.Pageable;
import org.project.enums.SupplierTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collection;

@Repository
public interface SupplierTransactionsRepository extends JpaRepository<SupplierTransactionsEntity, Long> {


    @Query("SELECT SUM(t.totalAmount) FROM SupplierTransactionsEntity t")
    BigDecimal findTotalRevenue();


    @Query("SELECT SUM(i.quantity) FROM SupplierTransactionItemEntity i WHERE i.productEntity.id = :productId AND i.supplierTransactionEntity.transactionType = org.project.enums.SupplierTransactionType.STOCK_OUT")
    Integer findSoldQuantityByProductId(@Param("productId") Long productId);


    List<SupplierTransactionsEntity> findTop4ByOrderByTransactionDateDesc();
    Collection<SupplierTransactionsEntity> findAllByTransactionType(SupplierTransactionType transactionType);
}
