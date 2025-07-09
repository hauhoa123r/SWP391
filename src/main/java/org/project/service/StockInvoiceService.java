package org.project.service;

import org.project.entity.StockInvoiceEntity;
import org.project.enums.StockTransactionType;

import java.util.List;

public interface StockInvoiceService {
    StockInvoiceEntity save(StockInvoiceEntity stockInvoice);
    StockInvoiceEntity findById(Long id);
    List<StockInvoiceEntity> findAll();
    List<StockInvoiceEntity> findAllByTransactionType(StockTransactionType transactionType);
    void deleteById(Long id);
} 