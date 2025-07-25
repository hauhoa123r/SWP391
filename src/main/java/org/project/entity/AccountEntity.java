package org.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.text.SimpleDateFormat;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="account_id",nullable = false)
	private long id;
	@Column(name="username", columnDefinition = "varchar(255)")
	private String username;
	@Column(name="email", columnDefinition = "varchar(255)")
	private String email;
	@Column(name="password_hash", columnDefinition = "varchar(255)")
	private String passwordHash;
	@Column(name="created_at", columnDefinition = "datetime")
	private SimpleDateFormat createdAt;
}
