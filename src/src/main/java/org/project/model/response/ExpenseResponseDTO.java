package org.project.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResponseDTO {
    private Long transactionId;
    private LocalDate transactionDate;
    private String supplierName;
    private BigDecimal totalAmount;

    // Constructors
    public ExpenseResponseDTO() {}

    public ExpenseResponseDTO(Long transactionId, LocalDate transactionDate, String supplierName, BigDecimal totalAmount) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.supplierName = supplierName;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
