package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierTransactionInvoiceMappingDTO {
    private Long supplierTransactionId;
    private Long supplierInvoiceId;
    private BigDecimal allocatedAmount;
    private String notes;
}