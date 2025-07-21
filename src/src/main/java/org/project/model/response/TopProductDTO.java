package org.project.model.response;

import java.math.BigDecimal;

public class TopProductDTO {
    private Long productId;
    private String productName;
    private BigDecimal totalRevenue;

    public TopProductDTO(Long productId, String productName, BigDecimal totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.totalRevenue = totalRevenue;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
