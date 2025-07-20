package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "medical_record_symptoms")
public class MedicalRecordSymptomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medical_record_id")
    private MedicalRecordEntity medicalRecordEntity;

    @Size(max = 255)
    @Column(name = "symptom_name")
    private String symptomName;

    @Column(name = "onset_date")
    private LocalDate onsetDate;

    @Size(max = 100)
    @Column(name = "duration", length = 100)
    private String duration;

    @Size(max = 100)
    @Column(name = "severity", length = 100)
    private String severity;

    @Lob
    @Column(name = "description")
    private String description;

}