package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockRequestResponse {
    private Long id;
    private SupplierTransactionType transactionType;
    private Timestamp requestDate;
    private SupplierResponse supplier;
    private InventoryManagerResponse requestedBy;
    private InventoryManagerResponse approvedBy;
    private SupplierTransactionStatus status;
    private Timestamp approvedDate;
    private String notes;
    private DepartmentResponse department;
    private Timestamp expectedDeliveryDate;
    private List<StockRequestItemResponse> items;
    private boolean hasInvoice;
} 