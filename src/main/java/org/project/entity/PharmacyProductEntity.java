package org.project.entity;

import java.math.BigDecimal;

import org.project.enums.ProductType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="pharmacy_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PharmacyProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="product_id", nullable = false)
	private long id;
	
	@Column(name="name",columnDefinition = "varchar(255)")
	private String name;
	
	@Column(name="type")
	private ProductType type;
	
	@Column(name = "price", columnDefinition = "DECIMAL", precision = 10, scale = 2)
	private BigDecimal price;
	
	@Column(name="unit", columnDefinition = "varchar(50)")
	private String unit;
	
	@Column(name="avatar", columnDefinition = "varchar(255)")
	private String avatar;
}
