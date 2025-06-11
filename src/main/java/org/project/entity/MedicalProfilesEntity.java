package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "medical_profiles", schema = "swp391")
public class MedicalProfilesEntity {
    @Id
    @Column(name = "medical_profile_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private PatientsEntity patient;

    @Size(max = 255)
    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "allergies")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> allergies;

    @Column(name = "chronic_diseases")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> chronicDiseases;

}