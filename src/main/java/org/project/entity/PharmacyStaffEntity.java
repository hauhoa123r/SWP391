package org.project.entity;

import java.text.SimpleDateFormat;

import org.project.enums.EmploymentType;
import org.project.enums.Gender;
import org.project.enums.PharmacyRole;
import org.project.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="pharmacy_staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PharmacyStaffEntity {
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
	@Column(name="role")
	private PharmacyRole role;
	@Column(name="pharmacy_role_note",columnDefinition = "text")
	private String pharmacyRoleNote;
}
