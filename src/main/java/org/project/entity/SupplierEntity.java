package org.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="supplier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SupplierEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="supplier_id",nullable = false)
	private long id;
	@Column(name="name", columnDefinition = "varchar(255)")
	private String name;
	@Column(name="email", columnDefinition = "varchar(255)")
	private String email;
	@Column(name="phone", columnDefinition = "varchar(50)")
	private String phone;
}
