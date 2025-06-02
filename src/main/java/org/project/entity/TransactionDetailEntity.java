package org.project.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="subscribed_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDetailEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="detail_id")
	private long id;
	
	@ManyToOne
	@JoinColumn(name="transaction_id", columnDefinition = "bigint")
	private SupplierTransactionEntity supplierTransactionEntity;
	
	@ManyToOne
	@JoinColumn(name="product_id", columnDefinition = "bigint")
	private PharmacyProductEntity pharmacyProductEntity;
	
	@Column(name="quantity")
	private int quantity;
	
	@Column(name = "unit_price", columnDefinition = "DECIMAL", precision = 10, scale = 2)
	private BigDecimal unitPrice;
}
