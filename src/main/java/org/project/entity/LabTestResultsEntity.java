package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lab_test_results", schema = "swp391")
public class LabTestResultsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_test_result_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_test_id")
    private LabTestsEntity labTest;

    @Column(name = "result_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> resultData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyzed_by")
    private StaffsEntity analyzedBy;

    @Column(name = "analyzed_at")
    private Timestamp analyzedAt;

}