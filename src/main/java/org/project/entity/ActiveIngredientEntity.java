package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="active_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActiveIngredientEntity {
	@Id
	@Column(name="ingredient_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="chemical_name")
	private String chemicalName;
	@Column(name="generic_name")
	private String genericName;
	@Column(name="description")
	private String description;
	@Column(name="therapeutic_class")
	private String therapeuticClass;
	@Column(name="atc_code")
	private String atcCode;
}
