package org.project.entity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;


import org.project.enums.OrderStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
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
	@ManyToOne
	@JoinColumn(name="patient_id")
	private PatientProfileEntity patientProfileEntity;
	
	private long staffId;
	
	private long appointmentId;
	
	@Column(name = "total", columnDefinition = "DECIMAL", precision = 10, scale = 2)
	private BigDecimal total;
	@Column(name="status")
	private OrderStatus status;
	
	@Column(name="created_at", columnDefinition = "datetime")
	private SimpleDateFormat createdAt;
}
