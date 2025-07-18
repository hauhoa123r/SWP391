package org.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
public class SupplierTransactionInvoiceMappingEntityId implements Serializable {
    private static final long serialVersionUID = -123456789012345678L;

    @Column(name = "supplier_transaction_id", nullable = false)
    private Long supplierTransactionId;

    @Column(name = "supplier_invoice_id", nullable = false)
    private Long supplierInvoiceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SupplierTransactionInvoiceMappingEntityId entity = (SupplierTransactionInvoiceMappingEntityId) o;
        return Objects.equals(this.supplierTransactionId, entity.supplierTransactionId) &&
                Objects.equals(this.supplierInvoiceId, entity.supplierInvoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierTransactionId, supplierInvoiceId);
    }
}