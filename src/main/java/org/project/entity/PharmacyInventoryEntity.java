package org.project.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="pharmacy_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PharmacyInventoryEntity {
	@Id
	@Column(name="inventory_id", nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="product_id", nullable = false)
	private long productId;
	
	@Column(name="current_stock")
	private int currentStock;
	
	@Column(name="minimum_stock")
	private int minimumStock;
}
