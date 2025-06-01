package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="prescription_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PrescriptionDetailEntity {
	@Id
	@Column(name="detail_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name="order_id")
	private PrescriptionOrderEntity prescriptionOrderEntity;
	
	@ManyToOne
	@JoinColumn(name="product_id")
	private PharmacyProductEntity PharmacyProductEntity;
	
	@ManyToOne
	@JoinColumn(name="ingredient_id")
	private ActiveIngredientEntity activeIngredientEntity;
	
	@Column(name="custom_strength")
	private String customStrength;
	
	@Column(name="dosage")
	private String dosage;
	
	@Column(name="duration_days")
	private int durationDay;
}
