package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cardiac_exams")
@Builder
public class CardiacExamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecordEntity medicalRecord;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Lob
    @Column(name = "heart_sounds")
    private String heartSounds;

    @Lob
    @Column(name = "murmur")
    private String murmur;

    @Size(max = 100)
    @Column(name = "jugular_venous_pressure", length = 100)
    private String jugularVenousPressure;

    @Size(max = 100)
    @Column(name = "edema", length = 100)
    private String edema;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "recorded_at")
    private Timestamp recordedAt;

}