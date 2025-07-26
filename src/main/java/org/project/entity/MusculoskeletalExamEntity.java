package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "musculoskeletal_exams")
@Builder
public class MusculoskeletalExamEntity {
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
    @Column(name = "joint_exam")
    private String jointExam;

    @Lob
    @Column(name = "muscle_strength")
    private String muscleStrength;

    @Lob
    @Column(name = "deformity")
    private String deformity;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "recorded_at")
    private Timestamp recordedAt;

}