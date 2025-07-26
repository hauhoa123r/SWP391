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
@Table(name = "genitourinary_exams")
@Builder
public class GenitourinaryExam {
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
    @Column(name = "kidney_area")
    private String kidneyArea;

    @Lob
    @Column(name = "bladder")
    private String bladder;

    @Lob
    @Column(name = "genital_inspection")
    private String genitalInspection;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "recorded_at")
    private Timestamp recordedAt;

}