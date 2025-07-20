package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "neurologic_exams")
@Builder
public class NeurologicExamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecordEntity medicalRecord;

    @Size(max = 100)
    @Column(name = "consciousness", length = 100)
    private String consciousness;

    @Lob
    @Column(name = "cranial_nerves")
    private String cranialNerves;

    @Lob
    @Column(name = "motor_function")
    private String motorFunction;

    @Lob
    @Column(name = "sensory_function")
    private String sensoryFunction;

    @Lob
    @Column(name = "reflexes")
    private String reflexes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "recorded_at")
    private Timestamp recordedAt;

}