package org.project.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="subscribed_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubscribedPackageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="subscription_id", nullable = false)
	private long id;
	
	private long patientId;
	
	@ManyToOne
	@JoinColumn(name="product_id")
	private PharmacyProductEntity pharmacyProductEntity;
	@Column(name="start_date", columnDefinition = "date")
	private Date startDate;
	@Column(name="end_date", columnDefinition = "date")
	private Date endDate;
}
