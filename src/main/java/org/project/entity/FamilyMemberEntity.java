package org.project.entity;

import java.util.Date;

import org.project.enums.Gender;
import org.project.enums.Relationship;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="family_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FamilyMemberEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="member_id", nullable = false)
	private long id;
	@ManyToOne
	@JoinColumn(name="account_id")
	private AccountEntity accountEntity;
	@Column(name="name", columnDefinition = "varchar(255)")
	private String name;
	@Column(name="relationship")
	private Relationship relationship;
	@Column(name="birth_date", columnDefinition = "date")
	private Date birthDate;
	@Column(name="gender")
	private Gender gender;
}
