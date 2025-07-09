package org.project.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockInvoiceDTO {
    
    @NotNull(message = "Stock request ID is required")
    private Long stockRequestId;
    
    @Size(max = 255, message = "Invoice number cannot exceed 255 characters")
    private String invoiceNumber;
    
    private BigDecimal totalAmount;
    
    private BigDecimal taxAmount;
    
    private BigDecimal shippingCost;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    @Size(max = 255, message = "Payment method cannot exceed 255 characters")
    private String paymentMethod;
    
    private Timestamp dueDate;
    
    private Timestamp paymentDate;
} 