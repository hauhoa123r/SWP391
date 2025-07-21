package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "patients", schema = "swp391")
public class PatientsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UsersEntity user;

    @Size(max = 255)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 255)
    @Column(name = "avatar")
    private String avatar;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Lob
    @Column(name = "relationship")
    private String relationship;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Lob
    @Column(name = "gender")
    private String gender;

    @Size(max = 255)
    @Column(name = "insurance_number")
    private String insuranceNumber;

    @Size(max = 255)
    @Column(name = "primary_contact")
    private String primaryContact;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "patient")
    private List<AppointmentsEntity> appointments;

    @OneToMany(mappedBy = "patient")
    private List<MedicalProfilesEntity> medicalProfiles;

}