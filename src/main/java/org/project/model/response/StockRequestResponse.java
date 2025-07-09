package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockRequestResponse {
    private Long id;
    private StockTransactionType transactionType;
    private Timestamp requestDate;
    private SupplierResponse supplier;
    private InventoryManagerResponse requestedBy;
    private InventoryManagerResponse approvedBy;
    private StockStatus status;
    private Timestamp approvedDate;
    private String notes;
    private DepartmentResponse department;
    private Timestamp expectedDeliveryDate;
    private List<StockRequestItemResponse> items;
    private boolean hasInvoice;
} 