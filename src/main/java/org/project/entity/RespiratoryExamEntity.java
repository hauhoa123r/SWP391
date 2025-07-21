package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "respiratory_exams")
public class RespiratoryExamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecordEntity medicalRecord;

    @Size(max = 50)
    @Column(name = "breathing_pattern", length = 50)
    private String breathingPattern;

    @Size(max = 50)
    @Column(name = "fremitus", length = 50)
    private String fremitus;

    @Size(max = 100)
    @Column(name = "percussion_note", length = 100)
    private String percussionNote;

    @Lob
    @Column(name = "auscultation")
    private String auscultation;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "recorded_at")
    private Instant recordedAt;

}