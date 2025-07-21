package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.time.Instant;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "gastrointestinal_exams")
@Builder
public class GastrointestinalExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecordEntity medicalRecord;

    @Lob
    @Column(name = "abdominal_inspection")
    private String abdominalInspection;

    @Lob
    @Column(name = "palpation")
    private String palpation;

    @Lob
    @Column(name = "percussion")
    private String percussion;

    @Lob
    @Column(name = "auscultation")
    private String auscultation;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "recorded_at")
    private Timestamp recordedAt;

}