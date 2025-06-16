package org.project.projection;

import java.math.BigDecimal;

public interface ProductViewProjection {
	Long getProductId();
    String getType();
    String getName();
    String getDescription();
    BigDecimal getPrice();
    String getUnit();
    String getProductStatus();
    Integer getStockQuantities();
    String getImageUrl();
    String getLabel();
    Long getCategoryId();
    String getTagName();
    String getAdditionalInfoName();
    String getAdditionalInfoValue();
}
