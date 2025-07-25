package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StocksDTO {
    private Long medicineId;
    private String medicineName;
    private String transactionType; // "IN" hoặc "OUT"
    private Integer quantity;
    private BigDecimal unitCost;
    private LocalDate expiryDate; // nếu cần cho thuốc
    private String batchNumber; // nếu cần
    private LocalDate transactionDate;
    private String sourceOrSupplierName; // tên NCC hoặc tên kho nguồn
}
