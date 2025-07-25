package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockReportResponse {
    private Long productId;
    private String productName;
    private Integer currentStock;
    private Integer threshold;
    private String status;
    private String recommendedAction;
    private Date expirationDate;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private int stockIn;
    private int stockOut;
    private BigDecimal stockInValue = BigDecimal.ZERO;
    private BigDecimal stockOutValue = BigDecimal.ZERO;
} 