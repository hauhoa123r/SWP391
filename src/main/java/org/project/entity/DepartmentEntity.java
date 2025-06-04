package org.project.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DepartmentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="department_id",nullable = false)
	private long id;
	@Column(name="name",columnDefinition = "varchar(255)")
	private String name;
	@Column(name="description",columnDefinition = "text")
	private String description;
	@ManyToOne
	@JoinColumn(name="staff_id")
	private MedicalStaffEntity medicalStaffEntity;
}
