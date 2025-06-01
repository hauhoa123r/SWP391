package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name="prescription_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PrescriptionOrderEntity {
	@Id
	@Column(name="order_id",nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
}
