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
	
	@Column(name="chemical_name",columnDefinition = "varchar(255)")
	private String chemicalName;
	@Column(name="generic_name",columnDefinition = "varchar(255)")
	private String genericName;
	@Column(name="description",columnDefinition = "text")
	private String description;
	@Column(name="therapeutic_class",columnDefinition = "varchar(255)")
	private String therapeuticClass;
	@Column(name="atc_code",columnDefinition = "varchar(50)")
	private String atcCode;
}
