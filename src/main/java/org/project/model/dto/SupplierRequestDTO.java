package org.project.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.SupplierTransactionType;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SupplierRequestDTO {
    
    @NotNull(message = "Transaction type is required")
    private SupplierTransactionType transactionType;
    
    private Long supplierId;
    
    private Long departmentId;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    private Timestamp expectedDeliveryDate;
    
    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<SupplierRequestItemDTO> items;
} 