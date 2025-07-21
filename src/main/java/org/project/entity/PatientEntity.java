package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.project.enums.BloodType;
import org.project.enums.FamilyRelationship;
import org.project.enums.Gender;
import org.project.enums.PatientStatus;
import org.project.enums.converter.BloodTypeConverter;

import java.sql.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@DynamicInsert
@Table(name = "patients", schema = "swp391")
@FieldNameConstants
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Size(max = 255)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 255)
    @Column(name = "identification_number", unique = true, nullable = false)
    private String identificationNumber;

    @NotNull
    @Column(name = "birthdate")
    private Date birthdate;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    @Column(name = "patient_status")
    private PatientStatus patientStatus = PatientStatus.ACTIVE;

    @OneToMany(mappedBy = "patientEntity")
    private Set<MedicalRecordEntity> medicalRecordEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "patientEntity")
    private Set<PricingPlanSubscriptionEntity> pricingPlanSubscriptionEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "patientEntity")
    private Set<AppointmentEntity> appointmentEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "patientEntity")
    private Set<ReviewEntity> reviewEntities = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'SELF'")
    @Column(name = "relationship")
    private FamilyRelationship familyRelationship;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'OTHER'")
    @Column(name = "gender", columnDefinition = "enum not null")
    private Gender gender;

    @Column(name = "blood_type", columnDefinition = "enum")
    @Convert(converter = BloodTypeConverter.class)
    private BloodType bloodType;

    @OneToOne(mappedBy = "patientEntity")
    private MedicalProfileEntity medicalProfileEntity;
}