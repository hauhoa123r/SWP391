package org.project.repository;

import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SupplierTransactionsRepository extends JpaRepository<SupplierTransactionsEntity, Long> {
    Collection<SupplierTransactionsEntity> findAllByTransactionType(SupplierTransactionType transactionType);
}
