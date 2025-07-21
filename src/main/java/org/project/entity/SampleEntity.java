package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "samples", schema = "swp391")
public class SampleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sample_id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "test_request_id", nullable = false)
    private TestRequestEntity testRequest;

    @Size(max = 50)
    @NotNull
    @Column(name = "barcode", nullable = false, length = 50)
    private String barcode;

    @ManyToOne
    @JoinColumn(name = "sampler_id")
    private UserEntity sampler;

    @NotNull
    @ColumnDefault("'pending'")
    @Lob
    @Column(name = "sample_status", nullable = false)
    private String sampleStatus;

    @Column(name = "collection_time")
    private Date collectionTime;

    @Lob
    @Column(name = "recollection_reason")
    private String recollectionReason;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "retest_time")
    private Instant retestTime;

    @OneToOne(mappedBy = "sampleEntity")
    private ResultEntity results;

}