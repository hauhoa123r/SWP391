package org.project.service;

import org.project.entity.StockRequestEntity;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;

import java.util.List;

public interface StockRequestService {
    StockRequestEntity save(StockRequestEntity stockRequest);
    StockRequestEntity findById(Long id);
    List<StockRequestEntity> findAll();
    List<StockRequestEntity> findAllByTransactionType(StockTransactionType transactionType);
    List<StockRequestEntity> findAllByStatus(StockStatus status);
    List<StockRequestEntity> findAllByTransactionTypeAndStatus(StockTransactionType transactionType, StockStatus status);
    void deleteById(Long id);
    void updateStatus(Long id, StockStatus status);
} 