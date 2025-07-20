package org.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class StockRequestItemEntityId implements Serializable {
    private static final long serialVersionUID = -9042377384759332847L;

    @NotNull
    @Column(name = "stock_request_id", nullable = false)
    private Long stockRequestId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockRequestItemEntityId entity = (StockRequestItemEntityId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.stockRequestId, entity.stockRequestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, stockRequestId);
    }
} 