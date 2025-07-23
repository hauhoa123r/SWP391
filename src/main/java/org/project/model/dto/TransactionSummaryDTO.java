package org.project.model.dto;

import lombok.Data;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO chứa thông tin tóm tắt về giao dịch cho dashboard
 */
@Data
public class TransactionSummaryDTO {
    private Long id;
    private String invoiceNumber;
    private SupplierTransactionType transactionType;
    private Timestamp transactionDate;
    private BigDecimal amount;
    private SupplierTransactionStatus status;
    private boolean isPaid;
} 