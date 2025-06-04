package org.project.entity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.project.enums.EmploymentType;
import org.project.enums.Gender;
import org.project.enums.OrderStatus;
import org.project.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="medical_staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MedicalStaffEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="staff_id",nullable = false)
	private long id;
	@ManyToOne
	@JoinColumn(name="account_id")
	private AccountEntity accountEntity;
	@Column(name="full_name",columnDefinition = "varchar(255)")
	private String fullName;
	@Column(name="birth_date",columnDefinition = "date")
	private SimpleDateFormat birthDate;
	@Column(name="gender")
	private Gender gender;
	@Column(name="employment_type")
	private EmploymentType employmentType;
	@ManyToOne
	@JoinColumn(name="department_id")
	private DepartmentEntity departmentEntity;
	@Column(name="license_number",columnDefinition = "varchar(255)")
	private String licenseNumber;
	@Column(name="status")
	private Status status;
	@Column(name="created_time",columnDefinition = "datetime")
	private SimpleDateFormat createdTime;
	
}
