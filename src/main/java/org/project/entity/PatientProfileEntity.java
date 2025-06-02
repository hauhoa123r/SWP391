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
public class PatientProfileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="patient_id",nullable = false)
	private long id;
	
	private long memberId;
	@Column(name="insurance_number",columnDefinition = "varchar(255)")
	private String insuranceNumber;
	@Column(name="primary_contact",columnDefinition = "varchar(20)")
	private String primaryContact;
	@Column(name="registered_at",columnDefinition = "datetime")
	private Date registeredAt;
}
