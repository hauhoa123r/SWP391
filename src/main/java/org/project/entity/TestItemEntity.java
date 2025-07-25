package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "test_items")
public class TestItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_item_id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 20)
    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "ref_min", precision = 10, scale = 2)
    private BigDecimal refMin;

    @Column(name = "ref_max", precision = 10, scale = 2)
    private BigDecimal refMax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_type_id")
    private TestTypeEntity testTypeEntity;

    @OneToMany(mappedBy = "testItemEntity")
    private Set<ReferenceRangeEntity> referenceRanges = new LinkedHashSet<>();

    @OneToMany(mappedBy = "testItemEntity")
    private Set<ResultDetailEntity> resultDetails = new LinkedHashSet<>();
}