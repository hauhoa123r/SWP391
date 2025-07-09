package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductStockReportResponse {
    private Long productId;
    private String productName;
    private Integer currentStock;
    private Integer threshold;
    private String status;
    private String recommendedAction;
    private Date expirationDate;
    private String batchNumber;
    private Integer stockIn = 0;
    private Integer stockOut = 0;
    private BigDecimal stockInValue = BigDecimal.ZERO;
    private BigDecimal stockOutValue = BigDecimal.ZERO;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
} 