package org.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="supplier_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SupplierTransactionEntity {
	@Id
	@Column(name="transaction_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name="supplier_id")
	private SupplierEntity supplierEntity;
	
	private long staffId;
	
	@Column(name = "total_amount", columnDefinition = "DECIMAL", precision = 10, scale = 2)
	private BigDecimal totalAmount;
	
	@Column(name="transaction_date", columnDefinition = "datetime")
	private Date transasctionDate;
}
