package org.project.model.response;

public class LowStockProductDTO {
    private Long productId;
    private String name;
    private Integer stockQuantities;

    public LowStockProductDTO(Long productId, String name, Integer stockQuantities) {
        this.productId = productId;
        this.name = name;
        this.stockQuantities = stockQuantities;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStockQuantities() {
        return stockQuantities;
    }

    public void setStockQuantities(Integer stockQuantities) {
        this.stockQuantities = stockQuantities;
    }
}
