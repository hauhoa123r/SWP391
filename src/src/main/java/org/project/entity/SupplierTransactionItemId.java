package org.project.entity;

import java.io.Serializable;
import java.util.Objects;

public class SupplierTransactionItemId implements Serializable {

    private Long supplierTransactionId;
    private Long productId;

    public SupplierTransactionItemId() {}

    public SupplierTransactionItemId(Long supplierTransactionId, Long productId) {
        this.supplierTransactionId = supplierTransactionId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupplierTransactionItemId)) return false;
        SupplierTransactionItemId that = (SupplierTransactionItemId) o;
        return Objects.equals(supplierTransactionId, that.supplierTransactionId)
                && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierTransactionId, productId);
    }
}
